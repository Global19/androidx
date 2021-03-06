/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.compiler.plugins.kotlin

import org.junit.Test

class ControlFlowTransformTestsNoSource : AbstractControlFlowTransformTests() {
    override val sourceInformationEnabled: Boolean get() = false

    @Test
    fun testPublicFunctionAlwaysMarkedAsCall(): Unit = controlFlow(
        """
            @Composable
            fun Test() {
              A(a)
              A(b)
            }
        """,
        """
            @Composable
            fun Test(%composer: Composer<*>?, %key: Int, %changed: Int) {
              %composer.startRestartGroup(<> xor %key, "C(Test)")
              if (%changed !== 0 || !%composer.skipping) {
                A(a, %composer, <>, 0)
                A(b, %composer, <>, 0)
              } else {
                %composer.skipToGroupEnd()
              }
              %composer.endRestartGroup()?.updateScope { %composer: Composer<*>?, %key: Int, %force: Int ->
                Test(%composer, %key, %changed or 0b0001)
              }
            }
        """
    )

    @Test
    fun testPrivateFunctionDoNotGetMarkedAsCall(): Unit = controlFlow(
        """
            @Composable
            private fun Test() {
              A(a)
              A(b)
            }
        """,
        """
            @Composable
            private fun Test(%composer: Composer<*>?, %key: Int, %changed: Int) {
              %composer.startRestartGroup(<> xor %key)
              if (%changed !== 0 || !%composer.skipping) {
                A(a, %composer, <>, 0)
                A(b, %composer, <>, 0)
              } else {
                %composer.skipToGroupEnd()
              }
              %composer.endRestartGroup()?.updateScope { %composer: Composer<*>?, %key: Int, %force: Int ->
                Test(%composer, %key, %changed or 0b0001)
              }
            }
        """
    )

    @Test
    fun testCallingAWrapperComposable(): Unit = controlFlow(
        """
            @Composable
            fun Test() {
              W {
                A()
              }
            }
        """,
        """
            @Composable
            fun Test(%composer: Composer<*>?, %key: Int, %changed: Int) {
              %composer.startRestartGroup(<> xor %key, "C(Test)")
              if (%changed !== 0 || !%composer.skipping) {
                W(composableLambda(%composer, <>, true, null) { %composer: Composer<*>?, %key: Int, %changed: Int ->
                  if (%changed and 0b0011 xor 0b0010 !== 0 || !%composer.skipping) {
                    A(%composer, <>, 0)
                  } else {
                    %composer.skipToGroupEnd()
                  }
                }, %composer, <>, 0b0110)
              } else {
                %composer.skipToGroupEnd()
              }
              %composer.endRestartGroup()?.updateScope { %composer: Composer<*>?, %key: Int, %force: Int ->
                Test(%composer, %key, %changed or 0b0001)
              }
            }
        """
    )

    @Test
    fun testCallingAnInlineWrapperComposable(): Unit = controlFlow(
        """
            @Composable
            fun Test() {
              IW {
                A()
              }
            }
        """,
        """
            @Composable
            fun Test(%composer: Composer<*>?, %key: Int, %changed: Int) {
              %composer.startRestartGroup(<> xor %key, "C(Test)")
              if (%changed !== 0 || !%composer.skipping) {
                IW({ %composer: Composer<*>?, %key: Int, %changed: Int ->
                  if (%changed and 0b0011 xor 0b0010 !== 0 || !%composer.skipping) {
                    A(%composer, <>, 0)
                  } else {
                    %composer.skipToGroupEnd()
                  }
                }, %composer, <>, 0)
              } else {
                %composer.skipToGroupEnd()
              }
              %composer.endRestartGroup()?.updateScope { %composer: Composer<*>?, %key: Int, %force: Int ->
                Test(%composer, %key, %changed or 0b0001)
              }
            }
        """
    )
}