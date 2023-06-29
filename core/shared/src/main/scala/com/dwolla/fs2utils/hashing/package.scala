package com.dwolla.fs2utils

import fs2.*

package object hashing {
  implicit class ByteArrayToHexString(as: Array[Byte]) {
    def toHexString: String =
      new String(Stream.emits(as).chunks.through(hexStringPipe).compile.to(Array))
  }

  private val alphabet: Vector[Char] = Vector('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

  val hexStringPipe: Pipe[Pure, Chunk[Byte], Char] = _.flatMap { bytes =>
    Stream.chunk(bytes.flatMap { byte =>
      val hi = alphabet((0xF0 & byte) >>> 4)
      val low = alphabet(0x0F & byte)

      Chunk(hi, low)
    })
  }

}
