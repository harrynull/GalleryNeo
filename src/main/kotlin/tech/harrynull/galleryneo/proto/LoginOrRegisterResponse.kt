// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: user.proto
package tech.harrynull.galleryneo.proto

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.WireField
import com.squareup.wire.internal.sanitize
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Nothing
import kotlin.String
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class LoginOrRegisterResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#BOOL"
  )
  val isSuccessful: Boolean? = null,
  @field:WireField(
    tag = 2,
    adapter = "tech.harrynull.galleryneo.proto.User#ADAPTER"
  )
  val user: User? = null,
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val failMessage: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<LoginOrRegisterResponse, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is LoginOrRegisterResponse) return false
    return unknownFields == other.unknownFields
        && isSuccessful == other.isSuccessful
        && user == other.user
        && failMessage == other.failMessage
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + isSuccessful.hashCode()
      result = result * 37 + user.hashCode()
      result = result * 37 + failMessage.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (isSuccessful != null) result += """isSuccessful=$isSuccessful"""
    if (user != null) result += """user=$user"""
    if (failMessage != null) result += """failMessage=${sanitize(failMessage)}"""
    return result.joinToString(prefix = "LoginOrRegisterResponse{", separator = ", ", postfix = "}")
  }

  fun copy(
    isSuccessful: Boolean? = this.isSuccessful,
    user: User? = this.user,
    failMessage: String? = this.failMessage,
    unknownFields: ByteString = this.unknownFields
  ): LoginOrRegisterResponse = LoginOrRegisterResponse(isSuccessful, user, failMessage,
      unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<LoginOrRegisterResponse> = object :
        ProtoAdapter<LoginOrRegisterResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      LoginOrRegisterResponse::class, 
      "type.googleapis.com/tech.harrynull.galleryneo.proto.LoginOrRegisterResponse"
    ) {
      override fun encodedSize(value: LoginOrRegisterResponse): Int = 
        ProtoAdapter.BOOL.encodedSizeWithTag(1, value.isSuccessful) +
        User.ADAPTER.encodedSizeWithTag(2, value.user) +
        ProtoAdapter.STRING.encodedSizeWithTag(3, value.failMessage) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: LoginOrRegisterResponse) {
        ProtoAdapter.BOOL.encodeWithTag(writer, 1, value.isSuccessful)
        User.ADAPTER.encodeWithTag(writer, 2, value.user)
        ProtoAdapter.STRING.encodeWithTag(writer, 3, value.failMessage)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): LoginOrRegisterResponse {
        var isSuccessful: Boolean? = null
        var user: User? = null
        var failMessage: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> isSuccessful = ProtoAdapter.BOOL.decode(reader)
            2 -> user = User.ADAPTER.decode(reader)
            3 -> failMessage = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return LoginOrRegisterResponse(
          isSuccessful = isSuccessful,
          user = user,
          failMessage = failMessage,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: LoginOrRegisterResponse): LoginOrRegisterResponse = value.copy(
        user = value.user?.let(User.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
