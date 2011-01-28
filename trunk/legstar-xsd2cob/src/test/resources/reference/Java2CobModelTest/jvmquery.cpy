       01  jvmQueryRequest.
         03  envVarNames PIC X(32) OCCURS 0 TO 10.
       01  jvmQueryReply.
         03  country PIC X(32).
         03  currencySymbol PIC X(32).
         03  envVarValues PIC X(32) OCCURS 0 TO 10.
         03  formattedDate PIC X(32).
         03  language PIC X(32).
