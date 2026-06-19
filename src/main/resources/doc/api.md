# 设备操作接口

操作一个设备，需要发送HTTP消息到192.168.43.134:8080端口。

## 名词解释

* did

  设备ID，设备唯一标识符，字符串

* siid

  服务实例ID，自然数

* piid

  属性实例ID，自然数

* aiid

  方法实例ID，自然数

* eiid

  事件实例ID，自然数

* pid

  属性ID，由did，siid，piid组成，用字符点间隔。

* aid

  方法ID，由did，siid，aiid组成，用字符点间隔。

* eid

  事件ID，由did，siid，eiid组成，用字符点间隔。

* status

  状态码，0代表成功，负数代表错误。

## 1.1 读属性

* 请求（一次可以读多个属性值）

    ```json
    GET /device/v1/properties?pid={pid},{pid}
    ```

* 应答（body

    ```json
    {
      "msg":"ok",
      "data": [
        {
          	"pid": "{pid}",
          	"value": {value}
        }
      ]
    }
    ```

  其中，msg: "ok"，表示请求执行完成。

## 1.2 写属性

* 请求（一次可以写多个属性值）

    ```json
    PUT /device/v1/properties
    
    [
    	{
    		"pid": "{pid}",
    		"value": {value}
    	}
    ]
    ```

* 应答（body）

    ```json
    {
      "msg": "ok",
      "data": [
        {
          "pid":"{pid}",
          "status":0
        }
      ]
    }
    ```

## 1.3 执行方法

* 请求

    ```http
    PUT /device/v1/actions
    
    [
        {
            "aid": "{aid}",
            "in": [
                {
                    "piid": {piid},
                    "values": [
                        {value}
                    ]
                }
            ]
        }
    ]
    ```

* 应答

    ```json
    {
        "msg": "ok",
        "data": [
            {
                "aid": "{aid}",
                "status": 0,
                "out": [
                    {
                        "piid": {piid},
                        "values": [
                            {value}
                        ]
                    },
                    {
                        "piid": {piid},
                        "values": [
                            {value}
                        ]
                    }
                ]
            }
        ]
    }
    ```