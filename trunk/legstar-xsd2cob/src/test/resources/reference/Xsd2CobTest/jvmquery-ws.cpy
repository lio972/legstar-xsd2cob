       01  JVMQueryException.
         03  R-message PIC X(32).
       01  queryJvm.
         03  arg0.
           05  envVarNames PIC X(32) OCCURS 0 TO 10.
       01  queryJvmResponse.
         03  R-return.
           05  country PIC X(32).
           05  currencySymbol PIC X(32).
           05  envVarValues PIC X(32) OCCURS 0 TO 10.
           05  formattedDate PIC X(32).
           05  language PIC X(32).
