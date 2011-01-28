       01  cultureInfoParameters.
         03  cultureCode PIC X(32).
         03  decimalNumber PIC S9(7)V9(2) COMP-3.
       01  cultureInfoReply.
         03  currencySymbol PIC X(32).
         03  displayCountry PIC X(32).
         03  displayLanguage PIC X(32).
         03  formattedDate PIC X(32).
         03  formattedDecimalNumber PIC X(32).
         03  serverCultureInfo.
           05  cultureCode PIC X(32).
           05  displayCountry PIC X(32).
           05  displayLanguage PIC X(32).
