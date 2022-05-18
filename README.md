## 环境要求

- jvm 1.8

## 打包

需在项目根路径下执行，生成jar包名称为`demo111-0.0.1-SNAPSHOT.jar`

```shell
./mvnw clean package -Dmaven.test.skip=true 
```

## 运行(非后台执行)

端口：默认8080，可通过-Dspring.port指定。

```shell
java -server -jar demo111-0.0.1-SNAPSHOT.jar
```

小提示：运行前应当进入制品包所在路径(默认为 `./target`)，或拷贝至指定运行目录后再执行上述命令`

## 接口说明

### 迁移

此接口只生成异步任务，返回任务批次号，通过定时任务(30秒运行一次)，完成迁移动作

- Path:/migrate
- Method: POST
- Content-Type: application/json
- RequestBody:

```json
{
  "sourceDataSource": {
    "url": "",
    // jdbc连接迁移数据库的地址
    "username": "",
    // 迁移数据库用户名
    "password": "",
    // 迁移数据库密码
    "tableName": "",
    // 迁移表名
    "isOracle": true
    // 是否是oracle数据库，默认false
  },
  "targetDataSource": {
    "url": "",
    // 目标jdbc连接数据库的地址
    "username": "",
    // 目标数据库用户名
    "password": "",
    // 目标数据库密码
    "tableName": ""
    // 目标表
  },
  "size": 50
  // 单次迁移的条数，默认50
}
```

- ResponseBody:

```text
错误提示/批次号
```

### 查询迁移任务状态

通过迁移任务批次号，查询对应任务状态

- Path:/info/{batchNo}
- Method: GET
- ResponseBody:

```json
{
  "batchNo": "",
  // 批次号
  "sourceTableName": "",
  // 迁移表名
  "targetTableName": "",
  // 目标表名
  "totalCount": 0,
  // 总数量
  "size": 0,
  // 每次同步大小
  "totalPage": 0,
  // 总页数
  "lastSyncPage": 0,
  // 最后一次同步成功的页码
  "lastRowId": 0,
  // 最后一次同步成功的rowId
  "batchState": 0,
  // 1进行中 2成功 3 失败
  "errorMessage": ""
  // 失败原因
}
```