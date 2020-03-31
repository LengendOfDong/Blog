# 转载：Spring data报错：Inferred type ‘S’ for type parameter ‘S’ is not within its bound

转载自：[https://blog.csdn.net/u012490335/article/details/80380299](https://blog.csdn.net/u012490335/article/details/80380299)<br/>
参照`org.springframework.data.repository.query.QueryByExampleExecutor` 源码：

`findOne`:

```
/**
 * Returns a single entity matching the given {@link Example} or {@literal null} if none was found.
 *
 * @param example must not be {@literal null}.
 * @return a single entity matching the given {@link Example} or {@link Optional#empty()} if none was found.
 * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the Example yields more than one result.
 */
&lt;S extends T&gt; Optional&lt;S&gt; findOne(Example&lt;S&gt; example);

```

参照 `org.springframework.data.repository.CrudRepository`源码：<br/>
`findById`:

```
/**
 * Retrieves an entity by its id.
 * 
 * @param id must not be {@literal null}.
 * @return the entity with the given id or {@literal Optional#empty()} if none found
 * @throws IllegalArgumentException if {@code id} is {@literal null}.
 */
Optional&lt;T&gt; findById(ID id);

```

`findOne(id)`用`findById(id).orElse(null)` 替换即可。
