      * ----------------------------------------------------------------
      * Generated copybook for jvmQueryRequest
      * ----------------------------------------------------------------
       01  jvmQueryRequest.
           03  OCCURS-COUNTERS--C.
             05  envVarNames--C PIC 9(9) COMP-5.
           03  envVarNames OCCURS 0 TO 10 DEPENDING ON envVarNames--C 
              PIC X(32).
      * ----------------------------------------------------------------
      * Generated copybook for jvmQueryReply
      * ----------------------------------------------------------------
       01  jvmQueryReply.
           03  OCCURS-COUNTERS--C.
             05  envVarValues--C PIC 9(9) COMP-5.
           03  country PIC X(32).
           03  currencySymbol PIC X(32).
           03  envVarValues OCCURS 0 TO 10 DEPENDING ON envVarValues--C 
              PIC X(32).
           03  formattedDate PIC X(32).
           03  language PIC X(32).
