package com.dwolla.fs2utils.hashing

import fs2._
import org.specs2.mutable.Specification

class HexStringsSpec extends Specification {

  "hexStringPipe" should {
    "hex some things" >> {
      Stream.emit(0x2).map(_.toByte).chunks.through(hexStringPipe).compile.toList must beEqualTo("02".toCharArray.toList)
    }

    "calculate the hex string from the bytes of emoji" >> {
      val example = "👩‍💻"
      new String(Stream.emit(example).through(text.utf8Encode).chunks.through(hexStringPipe).compile.toList.toArray) must beEqualTo(example.getBytes("UTF-8").toHexString)
    }
  }

}
