protoc -I=./protosServer --java_out=../Server/src ./protosServer/MsgIds.proto
protoc -I=./protosServer --java_out=../Server/src ./protosServer/MsgLogin.proto
protoc -I=./protosServer --java_out=../Server/src ./protosServer/MsgUser.proto
protoc -I=./protosServer --java_out=../Server/src ./protosServer/MsgError.proto
protoc -I=./protosServer --java_out=../Server/src ./protosServer/MsgChat.proto
protoc -I=./protosServer --java_out=../Server/src ./protosServer/MsgRelation.proto

pause