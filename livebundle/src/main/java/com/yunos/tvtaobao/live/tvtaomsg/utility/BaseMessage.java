//// Generated by the protocol buffer compiler.  DO NOT EDIT!
//
//package com.yunos.tvtaobao.live.tvtaomsg.utility;
//
//@SuppressWarnings("hiding")
//public interface BaseMessage {
//
//  public static final class ShareMessage extends
//      com.google.protobuf.nano.MessageNano {
//
//    private static volatile ShareMessage[] _emptyArray;
//    public static ShareMessage[] emptyArray() {
//      // Lazily initializes the empty array
//      if (_emptyArray == null) {
//        synchronized (
//            com.google.protobuf.nano.InternalNano.LAZY_INIT_LOCK) {
//          if (_emptyArray == null) {
//            _emptyArray = new ShareMessage[0];
//          }
//        }
//      }
//      return _emptyArray;
//    }
//
//    // optional string content = 1;
//    public String content;
//
//    // optional string title = 2;
//    public String title;
//
//    // optional string picUrl = 3;
//    public String picUrl;
//
//    // optional string actionUrl = 4;
//    public String actionUrl;
//
//    // optional string shareType = 5;
//    public String shareType;
//
//    // optional string shareId = 6;
//    public String shareId;
//
//    // optional string price = 7;
//    public String price;
//
//    public ShareMessage() {
//      clear();
//    }
//
//    public ShareMessage clear() {
//      content = "";
//      title = "";
//      picUrl = "";
//      actionUrl = "";
//      shareType = "";
//      shareId = "";
//      price = "";
//      cachedSize = -1;
//      return this;
//    }
//
//    @Override
//    public void writeTo(com.google.protobuf.nano.CodedOutputByteBufferNano output)
//        throws java.io.IOException {
//      if (!this.content.equals("")) {
//        output.writeString(1, this.content);
//      }
//      if (!this.title.equals("")) {
//        output.writeString(2, this.title);
//      }
//      if (!this.picUrl.equals("")) {
//        output.writeString(3, this.picUrl);
//      }
//      if (!this.actionUrl.equals("")) {
//        output.writeString(4, this.actionUrl);
//      }
//      if (!this.shareType.equals("")) {
//        output.writeString(5, this.shareType);
//      }
//      if (!this.shareId.equals("")) {
//        output.writeString(6, this.shareId);
//      }
//      if (!this.price.equals("")) {
//        output.writeString(7, this.price);
//      }
//      super.writeTo(output);
//    }
//
//    @Override
//    protected int computeSerializedSize() {
//      int size = super.computeSerializedSize();
//      if (!this.content.equals("")) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeStringSize(1, this.content);
//      }
//      if (!this.title.equals("")) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeStringSize(2, this.title);
//      }
//      if (!this.picUrl.equals("")) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeStringSize(3, this.picUrl);
//      }
//      if (!this.actionUrl.equals("")) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeStringSize(4, this.actionUrl);
//      }
//      if (!this.shareType.equals("")) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeStringSize(5, this.shareType);
//      }
//      if (!this.shareId.equals("")) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeStringSize(6, this.shareId);
//      }
//      if (!this.price.equals("")) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeStringSize(7, this.price);
//      }
//      return size;
//    }
//
//    @Override
//    public ShareMessage mergeFrom(
//            com.google.protobuf.nano.CodedInputByteBufferNano input)
//        throws java.io.IOException {
//      while (true) {
//        int tag = input.readTag();
//        switch (tag) {
//          case 0:
//            return this;
//          default: {
//            if (!com.google.protobuf.nano.WireFormatNano.parseUnknownField(input, tag)) {
//              return this;
//            }
//            break;
//          }
//          case 10: {
//            this.content = input.readString();
//            break;
//          }
//          case 18: {
//            this.title = input.readString();
//            break;
//          }
//          case 26: {
//            this.picUrl = input.readString();
//            break;
//          }
//          case 34: {
//            this.actionUrl = input.readString();
//            break;
//          }
//          case 42: {
//            this.shareType = input.readString();
//            break;
//          }
//          case 50: {
//            this.shareId = input.readString();
//            break;
//          }
//          case 58: {
//            this.price = input.readString();
//            break;
//          }
//        }
//      }
//    }
//
//    public static ShareMessage parseFrom(byte[] data)
//        throws com.google.protobuf.nano.InvalidProtocolBufferNanoException {
//      return com.google.protobuf.nano.MessageNano.mergeFrom(new ShareMessage(), data);
//    }
//
//    public static ShareMessage parseFrom(
//            com.google.protobuf.nano.CodedInputByteBufferNano input)
//        throws java.io.IOException {
//      return new ShareMessage().mergeFrom(input);
//    }
//  }
//
//  public static final class JoinNotify extends
//      com.google.protobuf.nano.MessageNano {
//
//    private static volatile JoinNotify[] _emptyArray;
//    public static JoinNotify[] emptyArray() {
//      // Lazily initializes the empty array
//      if (_emptyArray == null) {
//        synchronized (
//            com.google.protobuf.nano.InternalNano.LAZY_INIT_LOCK) {
//          if (_emptyArray == null) {
//            _emptyArray = new JoinNotify[0];
//          }
//        }
//      }
//      return _emptyArray;
//    }
//
//    // optional int32 totalCount = 1;
//    public int totalCount;
//
//    // optional int32 onlineCount = 2;
//    public int onlineCount;
//
//    // map<string, string> addUsers = 3;
//    public java.util.Map<String, String> addUsers;
//
//    // optional int64 pageViewCount = 4;
//    public long pageViewCount;
//
//    public JoinNotify() {
//      clear();
//    }
//
//    public JoinNotify clear() {
//      totalCount = 0;
//      onlineCount = 0;
//      addUsers = null;
//      pageViewCount = 0L;
//      cachedSize = -1;
//      return this;
//    }
//
//    @Override
//    public void writeTo(com.google.protobuf.nano.CodedOutputByteBufferNano output)
//        throws java.io.IOException {
//      if (this.totalCount != 0) {
//        output.writeInt32(1, this.totalCount);
//      }
//      if (this.onlineCount != 0) {
//        output.writeInt32(2, this.onlineCount);
//      }
//      if (this.addUsers != null) {
//        com.google.protobuf.nano.InternalNano.serializeMapField(
//          output, this.addUsers, 3,
//        com.google.protobuf.nano.InternalNano.TYPE_STRING,
//        com.google.protobuf.nano.InternalNano.TYPE_STRING);
//      }
//      if (this.pageViewCount != 0L) {
//        output.writeInt64(4, this.pageViewCount);
//      }
//      super.writeTo(output);
//    }
//
//    @Override
//    protected int computeSerializedSize() {
//      int size = super.computeSerializedSize();
//      if (this.totalCount != 0) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeInt32Size(1, this.totalCount);
//      }
//      if (this.onlineCount != 0) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeInt32Size(2, this.onlineCount);
//      }
//      if (this.addUsers != null) {
//        size += com.google.protobuf.nano.InternalNano.computeMapFieldSize(
//          this.addUsers, 3,
//        com.google.protobuf.nano.InternalNano.TYPE_STRING,
//        com.google.protobuf.nano.InternalNano.TYPE_STRING);
//      }
//      if (this.pageViewCount != 0L) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeInt64Size(4, this.pageViewCount);
//      }
//      return size;
//    }
//
//    @Override
//    public JoinNotify mergeFrom(
//            com.google.protobuf.nano.CodedInputByteBufferNano input)
//        throws java.io.IOException {
//      com.google.protobuf.nano.MapFactories.MapFactory mapFactory =
//        com.google.protobuf.nano.MapFactories.getMapFactory();
//      while (true) {
//        int tag = input.readTag();
//        switch (tag) {
//          case 0:
//            return this;
//          default: {
//            if (!com.google.protobuf.nano.WireFormatNano.parseUnknownField(input, tag)) {
//              return this;
//            }
//            break;
//          }
//          case 8: {
//            this.totalCount = input.readInt32();
//            break;
//          }
//          case 16: {
//            this.onlineCount = input.readInt32();
//            break;
//          }
//          case 26: {
//            this.addUsers = com.google.protobuf.nano.InternalNano.mergeMapEntry(
//              input, this.addUsers, mapFactory,
//              com.google.protobuf.nano.InternalNano.TYPE_STRING,
//              com.google.protobuf.nano.InternalNano.TYPE_STRING,
//              null,
//              10, 18);
//
//            break;
//          }
//          case 32: {
//            this.pageViewCount = input.readInt64();
//            break;
//          }
//        }
//      }
//    }
//
//    public static JoinNotify parseFrom(byte[] data)
//        throws com.google.protobuf.nano.InvalidProtocolBufferNanoException {
//      return com.google.protobuf.nano.MessageNano.mergeFrom(new JoinNotify(), data);
//    }
//
//    public static JoinNotify parseFrom(
//            com.google.protobuf.nano.CodedInputByteBufferNano input)
//        throws java.io.IOException {
//      return new JoinNotify().mergeFrom(input);
//    }
//  }
//
//  public static final class BizCount extends
//      com.google.protobuf.nano.MessageNano {
//
//    private static volatile BizCount[] _emptyArray;
//    public static BizCount[] emptyArray() {
//      // Lazily initializes the empty array
//      if (_emptyArray == null) {
//        synchronized (
//            com.google.protobuf.nano.InternalNano.LAZY_INIT_LOCK) {
//          if (_emptyArray == null) {
//            _emptyArray = new BizCount[0];
//          }
//        }
//      }
//      return _emptyArray;
//    }
//
//    // optional int64 value = 1;
//    public long value;
//
//    public BizCount() {
//      clear();
//    }
//
//    public BizCount clear() {
//      value = 0L;
//      cachedSize = -1;
//      return this;
//    }
//
//    @Override
//    public void writeTo(com.google.protobuf.nano.CodedOutputByteBufferNano output)
//        throws java.io.IOException {
//      if (this.value != 0L) {
//        output.writeInt64(1, this.value);
//      }
//      super.writeTo(output);
//    }
//
//    @Override
//    protected int computeSerializedSize() {
//      int size = super.computeSerializedSize();
//      if (this.value != 0L) {
//        size += com.google.protobuf.nano.CodedOutputByteBufferNano
//            .computeInt64Size(1, this.value);
//      }
//      return size;
//    }
//
//    @Override
//    public BizCount mergeFrom(
//            com.google.protobuf.nano.CodedInputByteBufferNano input)
//        throws java.io.IOException {
//      while (true) {
//        int tag = input.readTag();
//        switch (tag) {
//          case 0:
//            return this;
//          default: {
//            if (!com.google.protobuf.nano.WireFormatNano.parseUnknownField(input, tag)) {
//              return this;
//            }
//            break;
//          }
//          case 8: {
//            this.value = input.readInt64();
//            break;
//          }
//        }
//      }
//    }
//
//    public static BizCount parseFrom(byte[] data)
//        throws com.google.protobuf.nano.InvalidProtocolBufferNanoException {
//      return com.google.protobuf.nano.MessageNano.mergeFrom(new BizCount(), data);
//    }
//
//    public static BizCount parseFrom(
//            com.google.protobuf.nano.CodedInputByteBufferNano input)
//        throws java.io.IOException {
//      return new BizCount().mergeFrom(input);
//    }
//  }
//}