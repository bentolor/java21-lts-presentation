package bentolor.sandbox;

import java.lang.foreign.*;

class CallPid {
  public static void main(String... p) throws Throwable {
    var lookup = Linker.nativeLinker().defaultLookup();
    var libSymbol = lookup.find("getpid").orElseThrow();
    var nativeSig = FunctionDescriptor.of(ValueLayout.JAVA_LONG);

    Linker cABI = Linker.nativeLinker();
    var getpid = cABI.downcallHandle(libSymbol, nativeSig);

    System.out.println((long) getpid.invokeExact());                        
  }
}