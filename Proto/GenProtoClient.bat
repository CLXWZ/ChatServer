protogen\protogen -i:./protosClient/MsgIds.proto -o:../Client/Client/Assets/Code/Msg/MsgIds.cs -p:observable=true
protogen\protogen -i:./protosClient/MsgLogin.proto -o:../Client/Client/Assets/Code/Msg/MsgLogin.cs -p:observable=true
protogen\protogen -i:./protosClient/MsgUser.proto -o:../Client/Client/Assets/Code/Msg/MsgUser.cs -p:observable=true
protogen\protogen -i:./protosClient/MsgError.proto -o:../Client/Client/Assets/Code/Msg/MsgError.cs -p:observable=true
protogen\protogen -i:./protosClient/MsgChat.proto -o:../Client/Client/Assets/Code/Msg/MsgChat.cs -p:observable=true
protogen\protogen -i:./protosClient/MsgRelation.proto -o:../Client/Client/Assets/Code/Msg/MsgRelation.cs -p:observable=true

pause