      * ----------------------------------------------------------------
      * Generated copybook for dfhcommarea
      * ----------------------------------------------------------------
       01  dfhcommarea.
           03  OCCURS-COUNTERS--C.
             05  ListOdo--C PIC 9(9) COMP-5.
           03  ListOdo OCCURS 1 TO 100 DEPENDING ON ListOdo--C PIC X(5).
