
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("myairbuddyandi")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             myairbuddyandi

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
            myairbuddyandi.cpp
            libdivecomputer/include/libdivecomputer/version.h
            libdivecomputer/src/aes.c
            libdivecomputer/src/aes.h
            libdivecomputer/src/array.c
            libdivecomputer/src/array.h
            libdivecomputer/src/atomics_cobalt.c
            libdivecomputer/src/atomics_cobalt.h
            libdivecomputer/src/atomics_cobalt_parser.c
            libdivecomputer/src/bluetooth.c
            libdivecomputer/src/buffer.c
            libdivecomputer/src/checksum.c
            libdivecomputer/src/checksum.h
            libdivecomputer/src/citizen_aqualand.c
            libdivecomputer/src/citizen_aqualand.h
            libdivecomputer/src/citizen_aqualand_parser.c
            libdivecomputer/src/cochran_commander.c
            libdivecomputer/src/cochran_commander.h
            libdivecomputer/src/cochran_commander_parser.c
            libdivecomputer/src/common.c
            libdivecomputer/src/common-private.h
            libdivecomputer/src/context.c
            libdivecomputer/src/context-private.h
            libdivecomputer/src/cressi_edy.c
            libdivecomputer/src/cressi_edy.h
            libdivecomputer/src/cressi_edy_parser.c
            libdivecomputer/src/cressi_goa.c
            libdivecomputer/src/cressi_goa.h
            libdivecomputer/src/cressi_goa_parser.c
            libdivecomputer/src/cressi_leonardo.c
            libdivecomputer/src/cressi_leonardo.h
            libdivecomputer/src/cressi_leonardo_parser.c
            libdivecomputer/src/custom.c
            libdivecomputer/src/datetime.c
            libdivecomputer/src/deepblu_cosmiq.c
            libdivecomputer/src/deepblu_cosmiq.h
            libdivecomputer/src/deepblu_cosmiq_parser.c
            libdivecomputer/src/deepsix_excursion.c
            libdivecomputer/src/deepsix_excursion.h
            libdivecomputer/src/deepsix_excursion_parser.c
            libdivecomputer/src/descriptor.c
            libdivecomputer/src/descriptor-private.h
            libdivecomputer/src/device.c
            libdivecomputer/src/device-private.h
            libdivecomputer/src/diverite_nitekq.c
            libdivecomputer/src/diverite_nitekq.h
            libdivecomputer/src/diverite_nitekq_parser.c
            libdivecomputer/src/divesystem_idive.c
            libdivecomputer/src/divesystem_idive.h
            libdivecomputer/src/divesystem_idive_parser.c
            libdivecomputer/src/hw_frog.c
            libdivecomputer/src/hw_frog.h
            libdivecomputer/src/hw_ostc.c
            libdivecomputer/src/hw_ostc.h
            libdivecomputer/src/hw_ostc3.c
            libdivecomputer/src/hw_ostc3.h
            libdivecomputer/src/hw_ostc_parser.c
            libdivecomputer/src/ihex.c
            libdivecomputer/src/ihex.h
            libdivecomputer/src/iostream.c
            libdivecomputer/src/iostream-private.h
            libdivecomputer/src/irda.c
            libdivecomputer/src/iterator.c
            libdivecomputer/src/iterator-private.h
            libdivecomputer/src/libdivecomputer.rc
            libdivecomputer/src/libdivecomputer.symbols
            libdivecomputer/src/liquivision_lynx.c
            libdivecomputer/src/liquivision_lynx.h
            libdivecomputer/src/liquivision_lynx_parser.c
#            libdivecomputer/src/Makefile.am
            libdivecomputer/src/mares_common.c
            libdivecomputer/src/mares_common.h
            libdivecomputer/src/mares_darwin.c
            libdivecomputer/src/mares_darwin.h
            libdivecomputer/src/mares_darwin_parser.c
            libdivecomputer/src/mares_iconhd.c
            libdivecomputer/src/mares_iconhd.h
            libdivecomputer/src/mares_iconhd_parser.c
            libdivecomputer/src/mares_nemo.c
            libdivecomputer/src/mares_nemo.h
            libdivecomputer/src/mares_nemo_parser.c
            libdivecomputer/src/mares_puck.c
            libdivecomputer/src/mares_puck.h
            libdivecomputer/src/mclean_extreme.c
            libdivecomputer/src/mclean_extreme.h
            libdivecomputer/src/mclean_extreme_parser.c
            libdivecomputer/src/oceanic_atom2.c
            libdivecomputer/src/oceanic_atom2.h
            libdivecomputer/src/oceanic_atom2_parser.c
            libdivecomputer/src/oceanic_common.c
            libdivecomputer/src/oceanic_common.h
            libdivecomputer/src/oceanic_veo250.c
            libdivecomputer/src/oceanic_veo250.h
            libdivecomputer/src/oceanic_veo250_parser.c
            libdivecomputer/src/oceanic_vtpro.c
            libdivecomputer/src/oceanic_vtpro.h
            libdivecomputer/src/oceanic_vtpro_parser.c
            libdivecomputer/src/oceans_s1.c
            libdivecomputer/src/oceans_s1.h
            libdivecomputer/src/oceans_s1_common.c
            libdivecomputer/src/oceans_s1_common.h
            libdivecomputer/src/oceans_s1_parser.c
            libdivecomputer/src/parser.c
            libdivecomputer/src/parser-private.h
            libdivecomputer/src/platform.c
            libdivecomputer/src/platform.h
            libdivecomputer/src/rbstream.c
            libdivecomputer/src/rbstream.h
            libdivecomputer/src/reefnet_sensus.c
            libdivecomputer/src/reefnet_sensus.h
            libdivecomputer/src/reefnet_sensus_parser.c
            libdivecomputer/src/reefnet_sensuspro.c
            libdivecomputer/src/reefnet_sensuspro.h
            libdivecomputer/src/reefnet_sensuspro_parser.c
            libdivecomputer/src/reefnet_sensusultra.c
            libdivecomputer/src/reefnet_sensusultra.h
            libdivecomputer/src/reefnet_sensusultra_parser.c
            libdivecomputer/src/ringbuffer.c
            libdivecomputer/src/ringbuffer.h
            libdivecomputer/src/seac_screen.c
            libdivecomputer/src/seac_screen.h
            libdivecomputer/src/seac_screen_parser.c
            libdivecomputer/src/serial_posix.c
#            libdivecomputer/src/serial_win32.c
            libdivecomputer/src/shearwater_common.c
            libdivecomputer/src/shearwater_common.h
            libdivecomputer/src/shearwater_petrel.c
            libdivecomputer/src/shearwater_petrel.h
            libdivecomputer/src/shearwater_predator.c
            libdivecomputer/src/shearwater_predator.h
            libdivecomputer/src/shearwater_predator_parser.c
            libdivecomputer/src/socket.c
            libdivecomputer/src/socket.h
            libdivecomputer/src/sporasub_sp2.c
            libdivecomputer/src/sporasub_sp2.h
            libdivecomputer/src/sporasub_sp2_parser.c
            libdivecomputer/src/suunto_common.c
            libdivecomputer/src/suunto_common.h
            libdivecomputer/src/suunto_common2.c
            libdivecomputer/src/suunto_common2.h
            libdivecomputer/src/suunto_d9.c
            libdivecomputer/src/suunto_d9.h
            libdivecomputer/src/suunto_d9_parser.c
            libdivecomputer/src/suunto_eon.c
            libdivecomputer/src/suunto_eon.h
            libdivecomputer/src/suunto_eon_parser.c
            libdivecomputer/src/suunto_eonsteel.c
            libdivecomputer/src/suunto_eonsteel.h
            libdivecomputer/src/suunto_eonsteel_parser.c
            libdivecomputer/src/suunto_solution.c
            libdivecomputer/src/suunto_solution.h
            libdivecomputer/src/suunto_solution_parser.c
            libdivecomputer/src/suunto_vyper.c
            libdivecomputer/src/suunto_vyper.h
            libdivecomputer/src/suunto_vyper2.c
            libdivecomputer/src/suunto_vyper2.h
            libdivecomputer/src/suunto_vyper_parser.c
            libdivecomputer/src/tecdiving_divecomputereu.c
            libdivecomputer/src/tecdiving_divecomputereu.h
            libdivecomputer/src/tecdiving_divecomputereu_parser.c
            libdivecomputer/src/timer.c
            libdivecomputer/src/timer.h
            libdivecomputer/src/usb.c
            libdivecomputer/src/usbhid.c
            libdivecomputer/src/uwatec_aladin.c
            libdivecomputer/src/uwatec_aladin.h
            libdivecomputer/src/uwatec_memomouse.c
            libdivecomputer/src/uwatec_memomouse.h
            libdivecomputer/src/uwatec_memomouse_parser.c
            libdivecomputer/src/uwatec_smart.c
            libdivecomputer/src/uwatec_smart.h
            libdivecomputer/src/uwatec_smart_parser.c
            libdivecomputer/src/version.c
            libdivecomputer/src/zeagle_n2ition3.c
            libdivecomputer/src/zeagle_n2ition3.h
#            Add all xxx.c files here
#            No need to add the xxx.h
#            See include_directories( ${LOCAL_PATH}/libdivecomputer/include/ ) at the end of this file
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       myairbuddyandi

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

set(LOCAL_PATH ${CMAKE_CURRENT_LIST_DIR})

# for the *.h like including my own xxxx.h if any
include_directories( ${LOCAL_PATH}/libdivecomputer/include/ )
