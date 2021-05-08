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

class User(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val name: String? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val email: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<User, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is User) return false
    return unknownFields == other.unknownFields
        && name == other.name
        && email == other.email
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + name.hashCode()
      result = result * 37 + email.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (name != null) result += """name=${sanitize(name)}"""
    if (email != null) result += """email=${sanitize(email)}"""
    return result.joinToString(prefix = "User{", separator = ", ", postfix = "}")
  }

  fun copy(
    name: String? = this.name,
    email: String? = this.email,
    unknownFields: ByteString = this.unknownFields
  ): User = User(name, email, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<User> = object : ProtoAdapter<User>(
      FieldEncoding.LENGTH_DELIMITED, 
      User::class, 
      "type.googleapis.com/tech.harrynull.galleryneo.proto.User"
    ) {
      override fun encodedSize(value: User): Int = 
        ProtoAdapter.STRING.encodedSizeWithTag(1, value.name) +
        ProtoAdapter.STRING.encodedSizeWithTag(2, value.email) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: User) {
        ProtoAdapter.STRING.encodeWithTag(writer, 1, value.name)
        ProtoAdapter.STRING.encodeWithTag(writer, 2, value.email)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): User {
        var name: String? = null
        var email: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> name = ProtoAdapter.STRING.decode(reader)
            2 -> email = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return User(
          name = name,
          email = email,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: User): User = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
