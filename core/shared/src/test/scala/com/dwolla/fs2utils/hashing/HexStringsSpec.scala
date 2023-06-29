package com.dwolla.fs2utils.hashing

import fs2.*
import com.eed3si9n.expecty.Expecty.expect
import munit.FunSuite

class HexStringsSpec extends FunSuite {

  test("hexStringPipe should hex some things") {
    val output = Stream
      .emit(0x2)
      .map(_.toByte)
      .chunks
      .through(hexStringPipe)
      .compile
      .toList

    expect(output == "02".toCharArray.toList)
  }

  test("hexStringPipe should calculate the hex string from the bytes of emoji") {
    val example = "👩‍💻"
    val output = new String(Stream.emit(example).through(text.utf8.encode).chunks.through(hexStringPipe).compile.toList.toArray)
    expect(output == example.getBytes("UTF-8").toHexString)
  }

}
