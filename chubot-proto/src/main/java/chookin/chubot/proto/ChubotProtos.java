// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: command.proto

package chookin.chubot.proto;

public final class ChubotProtos {
  private ChubotProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface MasterProtoOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // optional int64 id = 1;
    /**
     * <code>optional int64 id = 1;</code>
     */
    boolean hasId();
    /**
     * <code>optional int64 id = 1;</code>
     */
    long getId();

    // required string method = 2;
    /**
     * <code>required string method = 2;</code>
     */
    boolean hasMethod();
    /**
     * <code>required string method = 2;</code>
     */
    java.lang.String getMethod();
    /**
     * <code>required string method = 2;</code>
     */
    com.google.protobuf.ByteString
        getMethodBytes();

    // optional string paras = 3;
    /**
     * <code>optional string paras = 3;</code>
     */
    boolean hasParas();
    /**
     * <code>optional string paras = 3;</code>
     */
    java.lang.String getParas();
    /**
     * <code>optional string paras = 3;</code>
     */
    com.google.protobuf.ByteString
        getParasBytes();
  }
  /**
   * Protobuf type {@code MasterProto}
   */
  public static final class MasterProto extends
      com.google.protobuf.GeneratedMessage
      implements MasterProtoOrBuilder {
    // Use MasterProto.newBuilder() to construct.
    private MasterProto(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private MasterProto(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final MasterProto defaultInstance;
    public static MasterProto getDefaultInstance() {
      return defaultInstance;
    }

    public MasterProto getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private MasterProto(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              id_ = input.readInt64();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              method_ = input.readBytes();
              break;
            }
            case 26: {
              bitField0_ |= 0x00000004;
              paras_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return chookin.chubot.proto.ChubotProtos.internal_static_MasterProto_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return chookin.chubot.proto.ChubotProtos.internal_static_MasterProto_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              chookin.chubot.proto.ChubotProtos.MasterProto.class, chookin.chubot.proto.ChubotProtos.MasterProto.Builder.class);
    }

    public static com.google.protobuf.Parser<MasterProto> PARSER =
        new com.google.protobuf.AbstractParser<MasterProto>() {
      public MasterProto parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new MasterProto(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<MasterProto> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // optional int64 id = 1;
    public static final int ID_FIELD_NUMBER = 1;
    private long id_;
    /**
     * <code>optional int64 id = 1;</code>
     */
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>optional int64 id = 1;</code>
     */
    public long getId() {
      return id_;
    }

    // required string method = 2;
    public static final int METHOD_FIELD_NUMBER = 2;
    private java.lang.Object method_;
    /**
     * <code>required string method = 2;</code>
     */
    public boolean hasMethod() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required string method = 2;</code>
     */
    public java.lang.String getMethod() {
      java.lang.Object ref = method_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          method_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string method = 2;</code>
     */
    public com.google.protobuf.ByteString
        getMethodBytes() {
      java.lang.Object ref = method_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        method_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // optional string paras = 3;
    public static final int PARAS_FIELD_NUMBER = 3;
    private java.lang.Object paras_;
    /**
     * <code>optional string paras = 3;</code>
     */
    public boolean hasParas() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional string paras = 3;</code>
     */
    public java.lang.String getParas() {
      java.lang.Object ref = paras_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          paras_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string paras = 3;</code>
     */
    public com.google.protobuf.ByteString
        getParasBytes() {
      java.lang.Object ref = paras_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        paras_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private void initFields() {
      id_ = 0L;
      method_ = "";
      paras_ = "";
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasMethod()) {
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
        output.writeInt64(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getMethodBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, getParasBytes());
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
          .computeInt64Size(1, id_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getMethodBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getParasBytes());
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

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof chookin.chubot.proto.ChubotProtos.MasterProto)) {
        return super.equals(obj);
      }
      chookin.chubot.proto.ChubotProtos.MasterProto other = (chookin.chubot.proto.ChubotProtos.MasterProto) obj;

      boolean result = true;
      result = result && (hasId() == other.hasId());
      if (hasId()) {
        result = result && (getId()
            == other.getId());
      }
      result = result && (hasMethod() == other.hasMethod());
      if (hasMethod()) {
        result = result && getMethod()
            .equals(other.getMethod());
      }
      result = result && (hasParas() == other.hasParas());
      if (hasParas()) {
        result = result && getParas()
            .equals(other.getParas());
      }
      result = result &&
          getUnknownFields().equals(other.getUnknownFields());
      return result;
    }

    private int memoizedHashCode = 0;
    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      if (hasId()) {
        hash = (37 * hash) + ID_FIELD_NUMBER;
        hash = (53 * hash) + hashLong(getId());
      }
      if (hasMethod()) {
        hash = (37 * hash) + METHOD_FIELD_NUMBER;
        hash = (53 * hash) + getMethod().hashCode();
      }
      if (hasParas()) {
        hash = (37 * hash) + PARAS_FIELD_NUMBER;
        hash = (53 * hash) + getParas().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static chookin.chubot.proto.ChubotProtos.MasterProto parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static chookin.chubot.proto.ChubotProtos.MasterProto parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(chookin.chubot.proto.ChubotProtos.MasterProto prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code MasterProto}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements chookin.chubot.proto.ChubotProtos.MasterProtoOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return chookin.chubot.proto.ChubotProtos.internal_static_MasterProto_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return chookin.chubot.proto.ChubotProtos.internal_static_MasterProto_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                chookin.chubot.proto.ChubotProtos.MasterProto.class, chookin.chubot.proto.ChubotProtos.MasterProto.Builder.class);
      }

      // Construct using chookin.chubot.proto.ChubotProtos.MasterProto.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
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
        id_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000001);
        method_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        paras_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return chookin.chubot.proto.ChubotProtos.internal_static_MasterProto_descriptor;
      }

      public chookin.chubot.proto.ChubotProtos.MasterProto getDefaultInstanceForType() {
        return chookin.chubot.proto.ChubotProtos.MasterProto.getDefaultInstance();
      }

      public chookin.chubot.proto.ChubotProtos.MasterProto build() {
        chookin.chubot.proto.ChubotProtos.MasterProto result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public chookin.chubot.proto.ChubotProtos.MasterProto buildPartial() {
        chookin.chubot.proto.ChubotProtos.MasterProto result = new chookin.chubot.proto.ChubotProtos.MasterProto(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.method_ = method_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.paras_ = paras_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof chookin.chubot.proto.ChubotProtos.MasterProto) {
          return mergeFrom((chookin.chubot.proto.ChubotProtos.MasterProto)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(chookin.chubot.proto.ChubotProtos.MasterProto other) {
        if (other == chookin.chubot.proto.ChubotProtos.MasterProto.getDefaultInstance()) return this;
        if (other.hasId()) {
          setId(other.getId());
        }
        if (other.hasMethod()) {
          bitField0_ |= 0x00000002;
          method_ = other.method_;
          onChanged();
        }
        if (other.hasParas()) {
          bitField0_ |= 0x00000004;
          paras_ = other.paras_;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasMethod()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        chookin.chubot.proto.ChubotProtos.MasterProto parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (chookin.chubot.proto.ChubotProtos.MasterProto) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // optional int64 id = 1;
      private long id_ ;
      /**
       * <code>optional int64 id = 1;</code>
       */
      public boolean hasId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>optional int64 id = 1;</code>
       */
      public long getId() {
        return id_;
      }
      /**
       * <code>optional int64 id = 1;</code>
       */
      public Builder setId(long value) {
        bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 id = 1;</code>
       */
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = 0L;
        onChanged();
        return this;
      }

      // required string method = 2;
      private java.lang.Object method_ = "";
      /**
       * <code>required string method = 2;</code>
       */
      public boolean hasMethod() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required string method = 2;</code>
       */
      public java.lang.String getMethod() {
        java.lang.Object ref = method_;
        if (!(ref instanceof java.lang.String)) {
          java.lang.String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          method_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string method = 2;</code>
       */
      public com.google.protobuf.ByteString
          getMethodBytes() {
        java.lang.Object ref = method_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          method_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string method = 2;</code>
       */
      public Builder setMethod(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        method_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string method = 2;</code>
       */
      public Builder clearMethod() {
        bitField0_ = (bitField0_ & ~0x00000002);
        method_ = getDefaultInstance().getMethod();
        onChanged();
        return this;
      }
      /**
       * <code>required string method = 2;</code>
       */
      public Builder setMethodBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        method_ = value;
        onChanged();
        return this;
      }

      // optional string paras = 3;
      private java.lang.Object paras_ = "";
      /**
       * <code>optional string paras = 3;</code>
       */
      public boolean hasParas() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional string paras = 3;</code>
       */
      public java.lang.String getParas() {
        java.lang.Object ref = paras_;
        if (!(ref instanceof java.lang.String)) {
          java.lang.String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          paras_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string paras = 3;</code>
       */
      public com.google.protobuf.ByteString
          getParasBytes() {
        java.lang.Object ref = paras_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          paras_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string paras = 3;</code>
       */
      public Builder setParas(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        paras_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string paras = 3;</code>
       */
      public Builder clearParas() {
        bitField0_ = (bitField0_ & ~0x00000004);
        paras_ = getDefaultInstance().getParas();
        onChanged();
        return this;
      }
      /**
       * <code>optional string paras = 3;</code>
       */
      public Builder setParasBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        paras_ = value;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:MasterProto)
    }

    static {
      defaultInstance = new MasterProto(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:MasterProto)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_MasterProto_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_MasterProto_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rcommand.proto\"8\n\013MasterProto\022\n\n\002id\030\001 \001" +
      "(\003\022\016\n\006method\030\002 \002(\t\022\r\n\005paras\030\003 \001(\tB)\n\024cho" +
      "okin.chubot.protoB\014ChubotProtosH\001\240\001\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_MasterProto_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_MasterProto_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_MasterProto_descriptor,
              new java.lang.String[] { "Id", "Method", "Paras", });
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
