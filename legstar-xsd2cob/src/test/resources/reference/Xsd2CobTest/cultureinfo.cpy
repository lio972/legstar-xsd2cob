      * ----------------------------------------------------------------
      * Generated copybook for CultureInfoException
      * ----------------------------------------------------------------
       01  CultureInfoException.
           03  R-message PIC X(32).
      * ----------------------------------------------------------------
      * Generated copybook for getInfo
      * ----------------------------------------------------------------
       01  getInfo.
           03  arg0.
             05  cultureCode PIC X(32).
             05  decimalNumber PIC S9(7)V9(2) COMP-3.
      * ----------------------------------------------------------------
      * Generated copybook for getInfoResponse
      * ----------------------------------------------------------------
       01  getInfoResponse.
           03  R-return.
             05  currencySymbol PIC X(32).
             05  displayCountry PIC X(32).
             05  displayLanguage PIC X(32).
             05  formattedDate PIC X(32).
             05  formattedDecimalNumber PIC X(32).
             05  serverCultureInfo.
               07  cultureCode PIC X(32).
               07  displayCountry PIC X(32).
               07  displayLanguage PIC X(32).
