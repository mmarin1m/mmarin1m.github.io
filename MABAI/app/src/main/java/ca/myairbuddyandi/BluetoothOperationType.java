/*
 *   Copyright (c) 2021 Martijn van Welie
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 *
 */

package ca.myairbuddyandi;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Michel on 2023-05-29.
 * This class represents the possible connection states
 */

// NOTE: Reserved for future use
public enum BluetoothOperationType {

    CONNECT(0),
    DISCONNECT(1),
    CHARACTERISTIC_WRITE(2),
    CHARACTERISTIC_READ(3),
    DESCRIPTOR_WRITE(4),
    DESCRIPTOR_READ(5),
    MTU_REQUEST(6),
    RUN(7),
    UNKNOWN_OPERATION(8)
    ;

    BluetoothOperationType(final int value) {
        this.value = value;
    }

    public final int value;

    @NotNull
    public static BluetoothOperationType fromValue(final int value) {
        for (BluetoothOperationType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNKNOWN_OPERATION;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
