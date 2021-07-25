## 数据库操作

- ### `select <库号>`: 切换库 默认共有15个

- ### `dbsize`: 查看当前库的key数量

- ### `flushdb`: 清空当前库

- ### `flushall`: 清空所有库

## Key的操作

- ### `keys *`： 查看当前库的所有key

- ### `exists <key>`: 判断该key是否存在

- ### `type <key>`: 查看该key的类型

- ### `de <key>`: 删除指定的key数据

- ### `unlink <key>`: 根据value选择非阻塞删除（仅从keyspace元数据中删除，真正的删除在后续的异步操作）

- ### `expire <key> <t>`: 设置key的过期时间为t  单位：秒

- ### `ttl <key>`: 查看指定key的剩余时间  -1为永不过期  -2为已过期

## String

- 说明
  - String是二进制安全的，任何对象只要能转化为字符串，就可以存储（图片，视频）
  - 一个字符串最多存储`512M`
- 常见命令
  - `set <key> <value>`: 添加数据
  - `getset <key> <value>`: 更新数据
  - `get <key>`: 取值
  - `append <key> <value>`: 指定value后追加值   (返回追加后的长度)
  - `strlen <key>`: 返回value长度
  - `setnx <key> <value>`: 当set不存在 添加value  若存在则不会覆盖
  - `incr <key>`: value++ 只能对数值操作 如果为空 加为1
  - `decr<key>`: value– 只能对数值操作 若为空 则为-1
  - `incrby/decrby <key> <n>`: + 或者 -  n
  - `mset<k1><v1><k2><v2>`: 同时设置多个键值对
  - `mget<k1><k2>`: 同时获取多个value
  - `msetnx <key> <value>`: 同时设置多个 当set不存在 添加value  若存在则不会覆盖 （原子性 一个失败全失败）
  - `gettrange <key> <起始位置><结束位置>`: 截取value返回
  - `settrange <key> <起始位置> <newValue>`: 从起始位置覆盖value为新值
  - `setex <key> <过期时间> <value>` : 新增值的同时设置过期时间。

## List 单键多值 （底层为双向链表）

- ### 常用命令

  - `lpush/rpush <key><v1><v2><v3> ` 从左边或者右边插入一个或多个值
    - 左边放是头插  （类似栈）
    - 右边放是尾插 （类似队列）
  - `lrange <key> <start> <stop>` 取start开始，stop结束的元素 
    - `lrange <key> 0 -1` 表示取所有值
  - `lpop/rpop`: 从左边或者右边弹值  （值空key亡）
  - `rpoplpush <key1> <key2>`:  k1右边弹值放入k2左边
  - `lindex <key>`: 获取指定下标的元素 从左到右
  - `llen <key>`: 获取list长度
  - `linsert <key> before <v1> <v2>`:将v2插入到左边第一次出现v1的位置之前。
  - `lrem <key> <n> <value>`: 从指定value处向左删除n个元素
  - `lset <key> <index> <newValue>`: 更新

## Set

- 对外的功能和list类似，但是可以`自动去重` 并且添加了`判断是否存在指定value`的功能
- 底层为value为null的hash表，添加，修改，删除复杂度都是O(1)
- 常见命令
  - `sadd <key> <v1><v2>` : 新增
  - `smembers <key>`: 取所有值
  - `sismember <key> <value>`: 判断是否存在该value 有返回1 无返回0
  - `scard <key>`: 长度
  - `srem <key> <value>`: 删除
  - `spop <key>`: 随机弹出一个值
  - `srandmember <key> <n>`: 随机取出n个值
  - `smove <key1> <key2> <value>`: 将value从key1放入key2中
  - `sinter <k1> <k2>` : 交集
  - `sunion <k2> <k1>`： 并集
  - `sdiff <k1> <k2>`:  k1中有 k2中没有的

## Hash

- 键值对集合

- String类型的``属性`和`值`的映射表  适合存储对象。 <String,Map>

- 例子

  ![image-20210717215636131](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210717215636131.png)

  ![image-20210717215523054](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210717215523054.png)

### 常见命令

- `hset <key><field1> <value1> <field2> <value2>` 给key中的field赋值value
- `hget <key> <field>`: 获取key对象中的field值
- `hexists<key><field>`: 查看key对象的field属性是否存在
- `hkeys <key>`: 列出所有field
- `hvals <key>`:列出所有value
- `hincrby <key> <field> <n>`： key对象的field属性加n
- `hsetnx <key> <field> <value>` : 当且仅当field不存在时 设置属性。

## Zset

在set的基础上增加了排序的功能，每个成员都关联了一个`评分(score)` ，根据评分进行排序.

- 常见命令
  - `zadd  <key><score1><value1><score2><value2>…`
    - 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
  - `zrange <key><start><stop>  [withscores]`
    - 返回有序集 key 中，下标在<start><stop>之间的元素
    - `zrange <key> 0 -1`:  返回所有元素
    - 带`withscores`，可以让分数一起和值返回到结果集。
  - `zrangebyscore <key> <min> <max> [withscores] [limit offset count]`
    - 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 

  - `zrevrangebyscore key maxmin [withscores] [limit offset count]`
    - 同上，改为从大到小排列。 

  - `zincrby <key> <n> <value> `   
    - 为value元素的score加上增n

  - `zrem  <key><value>`
    - 删除该集合下，指定值的元素 

  - `zcount <key> <min> <max>`
    - 统计该集合，分数区间内的元素个数 

  - `zrank <key><value>`
    - 返回该值在集合中的排名，从0开始。