# protoc-gen-p7g

Q：为什么要用gradle

A：使用gradle installDist编译可以更容易支持mac与winfows用户。

## 如何运行

Build the plugin

```shell
gradle installDist
```

Try it out!

```shell
protoc --plugin=protoc-gen-p7g=build/install/protoc-gen-p7g/bin/protoc-gen-p7g --p7g_out=. proto/*.proto
```

on Windows it would be something like:

```shell
protoc --plugin=protoc-gen-p7g=build/install/protoc-gen-p7g/bin/protoc-gen-p7g.bat --p7g_out=. proto/*.proto
```