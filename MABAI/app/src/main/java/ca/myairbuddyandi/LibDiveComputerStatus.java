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
 * Created by Michel on 2023-07-04.
 * This class represents the possible return status from libdivecomputer
 */

// NOTE: Reserved for future use
public enum
LibDiveComputerStatus {

    DC_STATUS_SUCCESS("Success"),
    DC_STATUS_CANCELLED("Cancelled"),
    DC_STATUS_DATAFORMAT("Data format error"),
    DC_STATUS_INVALIDARGS("Invalid arguments"),
    DC_STATUS_IO("Input/output error"),
    DC_STATUS_NOACCESS("Access denied"),
    DC_STATUS_NODEVICE("No device found"),
    DC_STATUS_NOMEMORY("Out of memory"),
    DC_STATUS_PROTOCOL("Protocol error"),
    DC_STATUS_TIMEOUT("Timeout"),
    DC_STATUS_UNSUPPORTED("Unsupported operation"),
    DC_UNKNOWN_ERROR("Unknown error")
    ;

    LibDiveComputerStatus(final String value) {
        this.value = value;
    }

    public final String value;

    @NotNull
    public static LibDiveComputerStatus fromValue(final String value) {
        for (LibDiveComputerStatus type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DC_UNKNOWN_ERROR;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
