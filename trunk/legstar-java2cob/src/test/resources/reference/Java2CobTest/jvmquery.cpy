       01  jvmQueryRequest.
          03  envVarNames--C PIC 9(9) COMP.
         03  envVarNames PIC X(32) OCCURS 0 TO 10 DEPENDING ON
             envVarNames--C.
       01  jvmQueryReply.
          03  envVarValues--C PIC 9(9) COMP.
         03  country PIC X(32).
         03  currencySymbol PIC X(32).
         03  envVarValues PIC X(32) OCCURS 0 TO 10 DEPENDING ON
             envVarValues--C.
         03  formattedDate PIC X(32).
         03  language PIC X(32).
