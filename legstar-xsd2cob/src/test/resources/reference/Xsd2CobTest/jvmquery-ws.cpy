       01  JVMQueryException.
         03  R-message PIC X(32).
       01  queryJvm.
          03  envVarNames--C PIC 9(9) COMP.
         03  arg0.
           05  envVarNames PIC X(32) OCCURS 0 TO 10 DEPENDING ON
             envVarNames--C.
       01  queryJvmResponse.
          03  envVarValues--C PIC 9(9) COMP.
         03  R-return.
           05  country PIC X(32).
           05  currencySymbol PIC X(32).
           05  envVarValues PIC X(32) OCCURS 0 TO 10 DEPENDING ON
             envVarValues--C.
           05  formattedDate PIC X(32).
           05  language PIC X(32).
