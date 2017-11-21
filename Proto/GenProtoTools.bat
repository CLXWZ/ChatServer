protoc -I=./protosServer --java_out=../Tools/src ./protosServer/MsgIds.proto
protoc -I=./protosServer --java_out=../Tools/src ./protosServer/MsgLogin.proto
protoc -I=./protosServer --java_out=../Tools/src ./protosServer/MsgUser.proto
protoc -I=./protosServer --java_out=../Tools/src ./protosServer/MsgError.proto
protoc -I=./protosServer --java_out=../Tools/src ./protosServer/MsgChat.proto
protoc -I=./protosServer --java_out=../Tools/src ./protosServer/MsgRelation.proto

pause