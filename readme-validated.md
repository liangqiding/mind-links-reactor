# 效验字段使用备注
```
@NotNull

# (""," ","   ")      不能为null，但可以为empty

@NotEmpty

# (" ","  ") 不能为null，而且长度必须大于0

@NotBlank

# 只能作用在String上，不能为null，而且调用trim()后，长度必须大于0 ("test")    即：必须有实际字符

```
