# 原创：PG批量删除表

通过使用存储过程的方式批量删除表<br/>
Create or replace function 过程名(参数名 参数类型,……) returns 返回值类型 as <math><semantics><mrow><mi>b</mi><mi>o</mi><mi>d</mi><mi>y</mi></mrow><annotation encoding="application/x-tex">body</annotation></semantics></math>body

具体应用：删除public用户下的所有表<br/>
CREATE FUNCTION del_ora_table() RETURNS void AS $$<br/>
DECLARE<br/>
tmp VARCHAR(512);<br/>
DECLARE names CURSOR FOR<br/>
select tablename from pg_tables where schemaname=‘public’;<br/>
BEGIN<br/>
FOR stmt IN names LOOP<br/>
tmp := 'DROP TABLE ‘|| quote_ident(stmt.tablename) || ’ CASCADE;’;<br/>
RAISE NOTICE ‘notice: %’, tmp;<br/>
EXECUTE 'DROP TABLE ‘|| quote_ident(stmt.tablename) || ’ CASCADE;’;<br/>
END LOOP;<br/>
RAISE NOTICE ‘finished …’;<br/>
END;

$$ LANGUAGE plpgsql;

–执行函数批量删除表<br/>
select del_ora_table();
