run.example.mac:
	# gradle installDist
	protoc --plugin=protoc-gen-p7g=build/install/protoc-gen-p7g/bin/protoc-gen-p7g \
		   --p7g_out=. \
		   --java_out=. \
		   proto/greeter.proto

run.example.win:
	protoc --plugin=protoc-gen-p7g=build/install/protoc-gen-p7g/bin/protoc-gen-p7g.bat --p7g_out=. proto/*.proto


