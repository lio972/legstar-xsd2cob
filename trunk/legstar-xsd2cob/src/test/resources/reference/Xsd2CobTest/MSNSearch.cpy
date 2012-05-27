      * ----------------------------------------------------------------
      * Generated copybook for R-Search
      * ----------------------------------------------------------------
       01  R-Search.
           03  OCCURS-COUNTERS--C.
             05  Flags--C PIC 9(9) COMP-5.
             05  SourceRequest--C PIC 9(9) COMP-5.
             05  SortBy--C PIC 9(9) COMP-5.
             05  ResultFields--C PIC 9(9) COMP-5.
             05  R-string--C PIC 9(9) COMP-5.
           03  Request.
             05  AppID PIC X(40).
             05  Query PIC X(128).
             05  CultureInfo PIC X(32).
             05  SafeSearch PIC X(32).
             05  Flags OCCURS 1 TO 10 DEPENDING ON Flags--C PIC X(32).
             05  Location.
               07  Latitude COMP-2.
               07  Longitude COMP-2.
               07  Radius COMP-2.
             05  Requests.
               07  SourceRequest OCCURS 0 TO 10 DEPENDING ON 
                   SourceRequest--C.
                 09  R-Source PIC X(32).
                 09  Offset PIC S9(9) COMP-5.
                 09  R-Count PIC S9(9) COMP-5.
                 09  FileType PIC X(32).
                 09  SortBy OCCURS 1 TO 10 DEPENDING ON SortBy--C PIC 
                     X(32).
                 09  ResultFields OCCURS 1 TO 10 DEPENDING ON 
                     ResultFields--C PIC X(32).
                 09  SearchTagFilters.
                   11  R-string OCCURS 0 TO 10 DEPENDING ON R-string--C 
                      PIC X(32).
      * ----------------------------------------------------------------
      * Generated copybook for SearchResponse
      * ----------------------------------------------------------------
       01  SearchResponse.
           03  OCCURS-COUNTERS--C.
             05  SourceResponse--C PIC 9(9) COMP-5.
             05  Result--C PIC 9(9) COMP-5.
             05  SearchTag--C PIC 9(9) COMP-5.
           03  Response.
             05  Responses.
               07  SourceResponse OCCURS 0 TO 10 DEPENDING ON 
                   SourceResponse--C.
                 09  R-Source PIC X(32).
                 09  Offset PIC S9(9) COMP-5.
                 09  Total PIC S9(9) COMP-5.
                 09  Results.
                   11  Result OCCURS 0 TO 10 DEPENDING ON Result--C.
                     13  R-Title PIC X(32).
                     13  Description PIC X(256).
                     13  Url PIC X(32).
                     13  DisplayUrl PIC X(32).
                     13  CacheUrl PIC X(32).
                     13  R-Source PIC X(32).
                     13  SearchTags PIC X(32).
                     13  Phone PIC X(32).
                     13  DateTime.
                       15  Year PIC S9(9) COMP-5.
                       15  Month PIC S9(9) COMP-5.
                       15  R-Day PIC S9(9) COMP-5.
                       15  Hour PIC S9(9) COMP-5.
                       15  Minute PIC S9(9) COMP-5.
                       15  Second PIC S9(9) COMP-5.
                     13  R-Address.
                       15  AddressLine PIC X(32).
                       15  PrimaryCity PIC X(32).
                       15  SecondaryCity PIC X(32).
                       15  Subdivision PIC X(32).
                       15  PostalCode PIC X(32).
                       15  CountryRegion PIC X(32).
                       15  FormattedAddress PIC X(32).
                     13  Location.
                       15  Latitude COMP-2.
                       15  Longitude COMP-2.
                       15  Radius COMP-2.
                     13  SearchTagsArray.
                       15  SearchTag OCCURS 0 TO 10 DEPENDING ON 
                           SearchTag--C.
                         17  Name PIC X(32).
                         17  R-Value PIC X(32).
                     13  Summary PIC X(32).
                     13  ResultType PIC X(32).
                     13  Image.
                       15  ImageURL PIC X(32).
                       15  ImageWidth PIC S9(9) COMP-5.
                       15  ImageHeight PIC S9(9) COMP-5.
                       15  ImageFileSize PIC S9(9) COMP-5.
                       15  ThumbnailURL PIC X(32).
                       15  ThumbnailWidth PIC S9(9) COMP-5.
                       15  ThumbnailHeight PIC S9(9) COMP-5.
                       15  ThumbnailFileSize PIC S9(9) COMP-5.
