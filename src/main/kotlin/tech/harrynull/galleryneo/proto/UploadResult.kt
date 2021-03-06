// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: gallery.proto
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

class UploadResult(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#BOOL"
  )
  val isSuccessful: Boolean? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  val failMessage: String? = null,
  @field:WireField(
    tag = 3,
    adapter = "tech.harrynull.galleryneo.proto.Image#ADAPTER"
  )
  val image: Image? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<UploadResult, Nothing>(ADAPTER, unknownFields) {
  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN
  )
  override fun newBuilder(): Nothing = throw AssertionError()

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is UploadResult) return false
    return unknownFields == other.unknownFields
        && isSuccessful == other.isSuccessful
        && failMessage == other.failMessage
        && image == other.image
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + isSuccessful.hashCode()
      result = result * 37 + failMessage.hashCode()
      result = result * 37 + image.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (isSuccessful != null) result += """isSuccessful=$isSuccessful"""
    if (failMessage != null) result += """failMessage=${sanitize(failMessage)}"""
    if (image != null) result += """image=$image"""
    return result.joinToString(prefix = "UploadResult{", separator = ", ", postfix = "}")
  }

  fun copy(
    isSuccessful: Boolean? = this.isSuccessful,
    failMessage: String? = this.failMessage,
    image: Image? = this.image,
    unknownFields: ByteString = this.unknownFields
  ): UploadResult = UploadResult(isSuccessful, failMessage, image, unknownFields)

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<UploadResult> = object : ProtoAdapter<UploadResult>(
      FieldEncoding.LENGTH_DELIMITED, 
      UploadResult::class, 
      "type.googleapis.com/tech.harrynull.galleryneo.proto.UploadResult"
    ) {
      override fun encodedSize(value: UploadResult): Int = 
        ProtoAdapter.BOOL.encodedSizeWithTag(1, value.isSuccessful) +
        ProtoAdapter.STRING.encodedSizeWithTag(2, value.failMessage) +
        Image.ADAPTER.encodedSizeWithTag(3, value.image) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: UploadResult) {
        ProtoAdapter.BOOL.encodeWithTag(writer, 1, value.isSuccessful)
        ProtoAdapter.STRING.encodeWithTag(writer, 2, value.failMessage)
        Image.ADAPTER.encodeWithTag(writer, 3, value.image)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): UploadResult {
        var isSuccessful: Boolean? = null
        var failMessage: String? = null
        var image: Image? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> isSuccessful = ProtoAdapter.BOOL.decode(reader)
            2 -> failMessage = ProtoAdapter.STRING.decode(reader)
            3 -> image = Image.ADAPTER.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return UploadResult(
          isSuccessful = isSuccessful,
          failMessage = failMessage,
          image = image,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: UploadResult): UploadResult = value.copy(
        image = value.image?.let(Image.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }
  }
}
