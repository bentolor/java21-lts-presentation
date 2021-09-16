package bentolor.sandbox;

import java.lang.foreign.*;
import java.lang.invoke.*;
import java.util.Arrays;

import static java.lang.foreign.ValueLayout.*;

public class RadixSort {
    public static void main(String[] args) throws Throwable {
        // 1. Find foreign function on the C library path
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = linker.defaultLookup();
        FunctionDescriptor funcDesc = FunctionDescriptor.ofVoid(ADDRESS, JAVA_INT, ADDRESS, JAVA_CHAR);
        MethodHandle radixsort = linker.downcallHandle(stdlib.find("radixsort").orElseThrow(), funcDesc);
        // 2. Allocate on-heap memory to store four strings
        String[] javaStrings = {"mouse", "cat", "dog", "car"};
        String[] sorted = {"car", "cat", "dog", "mouse"};
        int strCount = javaStrings.length;
        // 3. Use try-with-resources to manage the lifetime of off-heap memory
        try (Arena offHeap = Arena.ofConfined()) {
            // 4. Allocate a region of off-heap memory to store four pointers
            MemorySegment pointers = offHeap.allocateArray(ADDRESS, strCount);
            // 5. Copy the strings from on-heap to off-heap
            for (int i = 0; i < strCount; i++) {
                MemorySegment cString = offHeap.allocateUtf8String(javaStrings[i]);
                pointers.setAtIndex(ADDRESS, i, cString);
            }
            // 6. Sort the off-heap data by calling the foreign function
            radixsort.invoke(pointers, strCount, MemorySegment.NULL, '\0');
            // 7. Copy the (reordered) strings from off-heap to on-heap
            for (int i = 0; i < strCount; i++) {
                MemorySegment cString = pointers.getAtIndex(ADDRESS, i);
                cString = cString.reinterpret(Long.MAX_VALUE);
                javaStrings[i] = cString.getUtf8String(0);
            }
        } // 8. All off-heap memory is deallocated here
        assert Arrays.equals(javaStrings, sorted);
    }
}
