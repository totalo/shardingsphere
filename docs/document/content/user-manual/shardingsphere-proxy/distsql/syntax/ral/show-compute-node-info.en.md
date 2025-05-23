+++
title = "SHOW COMPUTE NODE INFO"
weight = 5
+++

### Description

The `SHOW COMPUTE NODE INFO` syntax is used to query current proxy instance information.
### Syntax

{{< tabs >}}
{{% tab name="Grammar" %}}
```sql
ShowComputeNodeInfo ::=
  'SHOW' 'COMPUTE' 'NODE' 'INFO'
```
{{% /tab %}}
{{% tab name="Railroad diagram" %}}
<iframe frameborder="0" name="diagram" id="diagram" width="100%" height="100%"></iframe>
{{% /tab %}}
{{< /tabs >}}

### Return Value Description

| Columns        | Description           |
|----------------|-----------------------|
| instance_id    | proxy instance id     |
| host           | host address          |
| port           | port number           |
| status         | proxy instance status |
| mode_type      | proxy instance mode   |
| worker_id      | worker id             |
| labels         | labels                |
| version       | version          |

### Example

- Query current proxy instance information

```sql
SHOW COMPUTE NODE INFO;
```

```sql
mysql> SHOW COMPUTE NODES;
+--------------------------------------+---------------+------------+------+--------+------------+-----------+--------+----------+
| instance_id                          | instance_type | host       | port | status | mode_type  | worker_id | labels | version  |
+--------------------------------------+---------------+------------+------+--------+------------+-----------+--------+----------+
| 3e84d33e-cb97-42f2-b6ce-f78fea0ded89 | PROXY         | 127.0.0.1  | 3307 | OK     | Cluster    | -1        |        | 5.4.2    |
+--------------------------------------+---------------+------------+------+--------+------------+-----------+--------+----------+
1 row in set (0.01 sec)
```

### Reserved word

`SHOW`, `COMPUTE`, `NODE`, `INFO`

### Related links

- [Reserved word](/en/user-manual/shardingsphere-proxy/distsql/syntax/reserved-word/)
