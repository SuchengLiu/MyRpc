# MyRpc
参考dubbo的轻量级rpc框架。

RPC（Remote Procedure Call）远程过程调用。对于远程服务器暴露出来的方法，我们可以像调用本利方法一样调用它，屏蔽掉一些网络细节。

主要模块：协议模块、序列化模块、网络模块、server模块、client模块、注册中心模块以及负载均衡模块

## 协议模块
Peer：表示网络传输的实体，用ip+port表示一个网络实体

ServiceDescriptor：表示服务描述符，包含服务接口名、方法名、方法返回类型以及方法参数类型。重写了hashCode()和equals()方法

Request：表示RPC的请求实体，包含服务描述法和方法参数值

Response：表示RPC的响应实体，包含响应码、返回的数据或错误信息

## 序列化模块
包括序列化和反序列化。

提供了基于FastJSON的实现

## 网络模块
使用Http协议传输服务的请求和相应。

- 客户端
  1. 建立连接
  2. 发送数据，并等待响应
  3. 关闭连接

客户端使用java.net包中的`HttpURLConnection`类创建和管理与Http服务器之间的连接。

HttpURLConnection默认使用长连接，因此没有提供显式的disconnect()方法，以便在之后的请求中可以复用同一连接。

- 服务端
  1. 启动并监听端口
  2. 等待客户端连接，收到请求后处理
  3. 关闭监听

服务端使用Jetty作为Http服务器。

`ServletContextHandler`类是Jetty中用于处理Servlet请求和管理Servlet上下文的关键组件。它提供了一种将Servlet与特定路径和上下文相关联的机制。允许在Jetty服务器上定义多个Servlet上下文，每个上下文都有自己的配置和处理规则。

`ServletHolder`类是Jetty服务器中用于持有Servlet实例的类。用于创建和管理Servlet实例，并将其添加到Jetty服务器的上下文中。

客户端的请求使用RequestServlet处理，它调用RequestHandler，但具体的处理逻辑则交给rpc-server模块实现

## server模块
rpc服务器
1. rpc服务器默认端点 127.0.0.1:3000
2. 注册和管理服务接口与服务提供者（服务实现类）
3. 向注册中心注册自己提供的服务
4. RequestHandler实现：解码数据，在服务器上查询并通过反射调用服务，返回数据；如果有异常，返回错误代码和错误信息。

`ServiceInstance`表示一个具体的服务，包括服务提供者的单例对象，以及提供的方法

`ServiceManager`通过`ConcurrentHashMap<ServiceDescriptor, ServiceInstance>`管理服务接口与服务实现类；通过反射机制获取服务接口中的公共方法（getDeclaredMethods()获取所有方法，再通过Modifier.isPublic()获取公共方法），并加入map。

`ServiceInvoker`通过反射机制调用具体的方法实现

## client模块
rpc客户端通过JDK动态代理生成服务代理对象，封装了远程调用的细节
1. 代理对象调用任何方法时都会把该方法和方法参数信息交给invoke函数
2. invoke函数通过invokeRemote进行远程调用
3. invokeRemote先向注册中心订阅服务提供者列表，再基于负载均衡策略选择一个服务提供者，客户端向该服务器发送请求，并等待服务器返回结果

代理实例的调用处理器需要实现InvocationHandler接口，并且每个代理实例都有一个关联的调用处理器。当一个方法在代理实例上被调用时，这个方法调用将被编码并分派到其调用处理器的invoke方法上。

RemoteInvoker实现了InvocationHandler

代理类具有以下属性：
1. 代理类的名称以 “$Proxy” 开头，后面跟着一个数字序号。
2. 代理类继承了Proxy类，其主要目的是为了传递InvocationHandler。
3. 代理类实现了创建时指定的接口（JDK动态代理是面向接口的）。
4. 每个代理类都有一个公共构造函数，它接受一个参数，即接口InvocationHandler的实现，用于设置代理实例的调用处理器。


## 注册中心模块
提供了服务注册和服务订阅的功能，注册中心存储的是每个服务对应的服务器地址

基于Zookeeper实现的注册中心
1. /rpc-service作为跟znode
2. 服务名称作为次级znode
3. 理论上，下层的znode为对应的类型，provider或者consumer，再下层是对应类型的机器，由ip+port构成路径的临时znode
4. 实际实现中，没有使用consumer类型，只有provider类型才向注册中心注册，因此将后两层简化为一层

基于Zookeeper实现的服务订阅机制
1. 先从本地缓存获取服务的提供者列表，获取不到再从zookeeper获取
2. 从zookeeper获取子节点时，Watcher 会被注册到指定节点的子节点上，用于监视子节点的变化。若子节点有变化，则重新获取最新子节点
3. 将zookeeper拉取的子节点信息转换为服务提供者列表，并存入本地缓存
4. 本地缓存的map可能存在并发安全问题 同时读写(subscribe读，watcher写)，因此使用ConcurrentHashMap实现

## 负载均衡模块
提供了随机负载均衡与一致性哈希负载均衡

一致性哈希负载均衡
1. 映射服务：将服务地址按照一定规则构造出特定的识别码（md5+位运算），再用识别码对2^32取模，确定服务在Hash值区间对应的位置。
2. 映射请求、定位服务：在发起请求时，我们往往会带上参数，而这些参数，就可以被我们用来确定具体调用哪一个服务。我们取服务Hash值大于请求Hash值的第一个服务作为实际的调用服务。
3. 新增服务节点、删除服务节点只影响部分请求。
4. 平衡性与虚拟节点：所谓虚拟节点，就是除了对服务本身地址进行Hash映射外，还通过在它地址上做些处理（比如Dubbo中，在ip+port的字符串后加上计数符1、2、3……，分别代表虚拟节点1、2、3），以达到同一服务映射多个节点的目的。通过引入虚拟节点，请求的分布就会比较平衡了。
