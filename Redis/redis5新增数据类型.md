## Bitmaps

- ### 概念

  （1） Bitmaps本身不是一种数据类型， 实际上它就是字符串（key-value） ， 但是它可以对字符串的位进行操作。

  （2） 可以把Bitmaps想象成一个以位为单位的数组， 数组的每个单元只能存储0和1， 数组的下标在Bitmaps中叫做偏移量。

  ​	![image-20210718164525814](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210718164525814.png)

- ### 常见命令

  - `setbit <key> <offset> <value>`:  设置Bitmaps中某个偏移量的值
  - `getbit<key><offset>`获取Bitmaps中某个偏移量的值
  - `bitcount <key> [start end] ` ： 统计值为1的数量
  - `bitop and/or/not/xor <resultKey> <key>`: 对key进行与或非操作结果放入resultKey中

- ### 举例

  每个独立用户是否访问过网站存放在Bitmaps中， 将访问的用户记做1， 没有访问的用户记做0， 用偏移量作为用户的id。

  设置键的第offset个位的值（从0算起） ， 假设现在有20个用户，userid=1， 6， 11， 15， 19的用户对网站进行了访问， 那么当前Bitmaps初始化结果如图

  ![img](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/wps1.jpg)

  - 初始化命令:   users:20210718 为key

    ​	![image-20210718165144404](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210718165144404.png)

  - 获取用户id为1 的用户是否访问过网站 结果为1 访问过

    ![image-20210718165343442](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210718165343442.png)

  - 统计今日的访问量

    ![image-20210718165738690](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210718165738690.png)

- ### Bitmaps与set对比

  - 举例

    假设网站有1亿用户， 每天独立访问的用户有5千万， 如果每天用集合类型和Bitmaps分别存储活跃用户可以得到

  |          |                    |                  |                        |
  | -------- | ------------------ | ---------------- | ---------------------- |
  | 数据类型 | 每个用户id占用空间 | 需要存储的用户量 | 全部内存量             |
  | 集合类型 | 64位               | 50000000         | 64位*50000000 = 400MB  |
  | Bitmaps  | 1位                | 100000000        | 1位*100000000 = 12.5MB |

  #### set和Bitmaps存储独立用户空间对比

  |          |        |        |       |
  | -------- | ------ | ------ | ----- |
  | 数据类型 | 一天   | 一个月 | 一年  |
  | 集合类型 | 400MB  | 12GB   | 144GB |
  | Bitmaps  | 12.5MB | 375MB  | 4.5GB |

## HyperLogLog

- ###  概念
  - ​	在工作当中，我们经常会遇到与统计相关的功能需求，比如统计网站PV（PageView页面访问量）,可以使用Redis的incr、incrby轻松实现。

  - 但像UV（UniqueVisitor，独立访客）、独立IP数、搜索记录数等需要去重和计数的问题如何解决？这种求集合中不重复元素个数的问题称为基数问题。

  - 解决基数问题有很多种方案：

    （1）数据存储在MySQL表中，使用distinct count计算不重复个数

    （2）使用Redis提供的hash、set、bitmaps等数据结构来处理

  - 以上的方案结果精确，但随着数据不断增加，导致占用空间越来越大，对于非常大的数据集是不切实际的。

  - 能否能够降低一定的精度来平衡存储空间？Redis推出了HyperLogLog

  - Redis HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，在输入元素的数量或者体积非常非常大时，计算基数所需的空间总是固定的、并且是很小的。

  - 在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。

  - 但是，因为 HyperLogLog 只会根据输入元素来计算基数，而不会储存输入元素本身，所以 HyperLogLog 不能像集合那样，返回输入的各个元素。

  - 什么是基数?

    - 比如数据集 {1, 3, 5, 7, 5, 7, 8}， 那么这个数据集的基数集为 {1, 3, 5 ,7, 8}, 基数(不重复元素)为5。 基数估计就是在误差可接受的范围内，快速计算基数。

- ### 命令

  - `pfadd <key>< element> [element ...] ` 添加指定元素到 HyperLogLog 中

## Geospatial

- ### 概念

  Redis 3.2 中增加了对GEO类型的支持。GEO，Geographic，地理信息的缩写。该类型，就是元素的2维坐标，在地图上就是经纬度。redis基于该类型，提供了经纬度设置，查询，范围查询，距离查询，经纬度Hash等常见操作。

- ### 命令

  - `geoadd<key> <longitude> <latitude> <member> [longitude latitude member...] ` 添加地理位置（经度，纬度，名称）

    ![image-20210718171625579](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210718171625579.png)

  - `geopos  <key><member> [member...]`  获得指定地区的坐标值

    ![image-20210718171728017](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210718171728017.png)

  - `geodist<key><member1><member2>[m|km|ft|mi] `  取得两个位置的距离 （mi 英里 ft英尺）

    ![image-20210718172028777](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210718172028777.png)

  - `georadius<key><longitude><latitude>radius m|km|ft|mi`  以给定的经纬度为中心，找出某一半径内的元素

    `georadius key 经度 纬度 半径 单位`