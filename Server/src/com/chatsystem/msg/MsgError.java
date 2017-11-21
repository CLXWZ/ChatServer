// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MsgError.proto

package com.chatsystem.msg;

public final class MsgError {
  private MsgError() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public enum ERROR_TYPE
      implements com.google.protobuf.ProtocolMessageEnum {
    Success(0, 0),
    ParamError(1, 1),
    SelfIsNotTarget(2, 2),
    TargetOffline(3, 3),
    TargetNoFriend(4, 4),
    TargetIsFriend(5, 5),
    NoUser(6, 6),
    IsInChatGroup(7, 7),
    NotInChatGroup(8, 8),
    BanLogin(9, 9),
    BanChat(10, 10),
    HasAccount(11, 11),
    NoAccount(12, 12),
    AuthError(13, 13),
    HasName(14, 14),
    ;
    
    public static final int Success_VALUE = 0;
    public static final int ParamError_VALUE = 1;
    public static final int SelfIsNotTarget_VALUE = 2;
    public static final int TargetOffline_VALUE = 3;
    public static final int TargetNoFriend_VALUE = 4;
    public static final int TargetIsFriend_VALUE = 5;
    public static final int NoUser_VALUE = 6;
    public static final int IsInChatGroup_VALUE = 7;
    public static final int NotInChatGroup_VALUE = 8;
    public static final int BanLogin_VALUE = 9;
    public static final int BanChat_VALUE = 10;
    public static final int HasAccount_VALUE = 11;
    public static final int NoAccount_VALUE = 12;
    public static final int AuthError_VALUE = 13;
    public static final int HasName_VALUE = 14;
    
    
    public final int getNumber() { return value; }
    
    public static ERROR_TYPE valueOf(int value) {
      switch (value) {
        case 0: return Success;
        case 1: return ParamError;
        case 2: return SelfIsNotTarget;
        case 3: return TargetOffline;
        case 4: return TargetNoFriend;
        case 5: return TargetIsFriend;
        case 6: return NoUser;
        case 7: return IsInChatGroup;
        case 8: return NotInChatGroup;
        case 9: return BanLogin;
        case 10: return BanChat;
        case 11: return HasAccount;
        case 12: return NoAccount;
        case 13: return AuthError;
        case 14: return HasName;
        default: return null;
      }
    }
    
    public static com.google.protobuf.Internal.EnumLiteMap<ERROR_TYPE>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<ERROR_TYPE>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ERROR_TYPE>() {
            public ERROR_TYPE findValueByNumber(int number) {
              return ERROR_TYPE.valueOf(number);
            }
          };
    
    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.chatsystem.msg.MsgError.getDescriptor().getEnumTypes().get(0);
    }
    
    private static final ERROR_TYPE[] VALUES = {
      Success, ParamError, SelfIsNotTarget, TargetOffline, TargetNoFriend, TargetIsFriend, NoUser, IsInChatGroup, NotInChatGroup, BanLogin, BanChat, HasAccount, NoAccount, AuthError, HasName, 
    };
    
    public static ERROR_TYPE valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }
    
    private final int index;
    private final int value;
    
    private ERROR_TYPE(int index, int value) {
      this.index = index;
      this.value = value;
    }
    
    // @@protoc_insertion_point(enum_scope:com.chatsystem.msg.ERROR_TYPE)
  }
  
  public interface MsErrorOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // required .com.chatsystem.msg.ERROR_TYPE Error = 1;
    boolean hasError();
    com.chatsystem.msg.MsgError.ERROR_TYPE getError();
  }
  public static final class MsError extends
      com.google.protobuf.GeneratedMessage
      implements MsErrorOrBuilder {
    // Use MsError.newBuilder() to construct.
    private MsError(Builder builder) {
      super(builder);
    }
    private MsError(boolean noInit) {}
    
    private static final MsError defaultInstance;
    public static MsError getDefaultInstance() {
      return defaultInstance;
    }
    
    public MsError getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.chatsystem.msg.MsgError.internal_static_com_chatsystem_msg_MsError_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.chatsystem.msg.MsgError.internal_static_com_chatsystem_msg_MsError_fieldAccessorTable;
    }
    
    private int bitField0_;
    // required .com.chatsystem.msg.ERROR_TYPE Error = 1;
    public static final int ERROR_FIELD_NUMBER = 1;
    private com.chatsystem.msg.MsgError.ERROR_TYPE error_;
    public boolean hasError() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public com.chatsystem.msg.MsgError.ERROR_TYPE getError() {
      return error_;
    }
    
    private void initFields() {
      error_ = com.chatsystem.msg.MsgError.ERROR_TYPE.Success;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      if (!hasError()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeEnum(1, error_.getNumber());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, error_.getNumber());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static com.chatsystem.msg.MsgError.MsError parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.chatsystem.msg.MsgError.MsError parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.chatsystem.msg.MsgError.MsError parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.chatsystem.msg.MsgError.MsError parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.chatsystem.msg.MsgError.MsError parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.chatsystem.msg.MsgError.MsError parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.chatsystem.msg.MsgError.MsError parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.chatsystem.msg.MsgError.MsError parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.chatsystem.msg.MsgError.MsError parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.chatsystem.msg.MsgError.MsError parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.chatsystem.msg.MsgError.MsError prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.chatsystem.msg.MsgError.MsErrorOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.chatsystem.msg.MsgError.internal_static_com_chatsystem_msg_MsError_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.chatsystem.msg.MsgError.internal_static_com_chatsystem_msg_MsError_fieldAccessorTable;
      }
      
      // Construct using com.chatsystem.msg.MsgError.MsError.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        error_ = com.chatsystem.msg.MsgError.ERROR_TYPE.Success;
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.chatsystem.msg.MsgError.MsError.getDescriptor();
      }
      
      public com.chatsystem.msg.MsgError.MsError getDefaultInstanceForType() {
        return com.chatsystem.msg.MsgError.MsError.getDefaultInstance();
      }
      
      public com.chatsystem.msg.MsgError.MsError build() {
        com.chatsystem.msg.MsgError.MsError result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private com.chatsystem.msg.MsgError.MsError buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        com.chatsystem.msg.MsgError.MsError result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public com.chatsystem.msg.MsgError.MsError buildPartial() {
        com.chatsystem.msg.MsgError.MsError result = new com.chatsystem.msg.MsgError.MsError(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.error_ = error_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.chatsystem.msg.MsgError.MsError) {
          return mergeFrom((com.chatsystem.msg.MsgError.MsError)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.chatsystem.msg.MsgError.MsError other) {
        if (other == com.chatsystem.msg.MsgError.MsError.getDefaultInstance()) return this;
        if (other.hasError()) {
          setError(other.getError());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasError()) {
          
          return false;
        }
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              onChanged();
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                onChanged();
                return this;
              }
              break;
            }
            case 8: {
              int rawValue = input.readEnum();
              com.chatsystem.msg.MsgError.ERROR_TYPE value = com.chatsystem.msg.MsgError.ERROR_TYPE.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(1, rawValue);
              } else {
                bitField0_ |= 0x00000001;
                error_ = value;
              }
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // required .com.chatsystem.msg.ERROR_TYPE Error = 1;
      private com.chatsystem.msg.MsgError.ERROR_TYPE error_ = com.chatsystem.msg.MsgError.ERROR_TYPE.Success;
      public boolean hasError() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public com.chatsystem.msg.MsgError.ERROR_TYPE getError() {
        return error_;
      }
      public Builder setError(com.chatsystem.msg.MsgError.ERROR_TYPE value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        error_ = value;
        onChanged();
        return this;
      }
      public Builder clearError() {
        bitField0_ = (bitField0_ & ~0x00000001);
        error_ = com.chatsystem.msg.MsgError.ERROR_TYPE.Success;
        onChanged();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:com.chatsystem.msg.MsError)
    }
    
    static {
      defaultInstance = new MsError(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.chatsystem.msg.MsError)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_chatsystem_msg_MsError_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_chatsystem_msg_MsError_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016MsgError.proto\022\022com.chatsystem.msg\"8\n\007" +
      "MsError\022-\n\005Error\030\001 \002(\0162\036.com.chatsystem." +
      "msg.ERROR_TYPE*\202\002\n\nERROR_TYPE\022\013\n\007Success" +
      "\020\000\022\016\n\nParamError\020\001\022\023\n\017SelfIsNotTarget\020\002\022" +
      "\021\n\rTargetOffline\020\003\022\022\n\016TargetNoFriend\020\004\022\022" +
      "\n\016TargetIsFriend\020\005\022\n\n\006NoUser\020\006\022\021\n\rIsInCh" +
      "atGroup\020\007\022\022\n\016NotInChatGroup\020\010\022\014\n\010BanLogi" +
      "n\020\t\022\013\n\007BanChat\020\n\022\016\n\nHasAccount\020\013\022\r\n\tNoAc" +
      "count\020\014\022\r\n\tAuthError\020\r\022\013\n\007HasName\020\016"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_com_chatsystem_msg_MsError_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_com_chatsystem_msg_MsError_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_chatsystem_msg_MsError_descriptor,
              new java.lang.String[] { "Error", },
              com.chatsystem.msg.MsgError.MsError.class,
              com.chatsystem.msg.MsgError.MsError.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
