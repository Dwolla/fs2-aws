package com.dwolla.fs2aws.cloudformation

import com.amazonaws.services.cloudformation.model.StackStatus

case class StackNotUpdatableException(private val name: String,
                                      private val status: StackStatus) extends RuntimeException(s"Stack $name is in status $status, which cannot be updated.")
