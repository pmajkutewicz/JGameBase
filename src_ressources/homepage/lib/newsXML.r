REBOL [Title: "Weblog XML Builder"
      File: %weblogXML.r
]

loaddata: func ["Load the files, build the db block and sort"
          where /local whichfiles date eintrag file
          ][
          structure: copy []         ; initialize
          content: copy []           ; initialize OUTPUT
          files-block: read where     ; file 1, file 2,...
          entries: make block! 100
          foreach file files-block [
                  date: copy []
                  eintrag: read/lines where/:file
                  while [empty-string: find eintrag ""][remove empty-string]
                  date: remove/part copy eintrag/2 5
                  entries: append entries to-date trim date
                  entries: append/only entries eintrag
                  ]
          sort/reverse/skip entries 2   ; SORT all entries of db
          if verbose [print rejoin ["Found " length? files-block " Files"]]
          entries
          ]

renderdata: func ["Parse for Keywords and build the structure"
            entries /local para
            ][
            foreach item entries [
                    parsed?: parse item [
                    "Date:" copy para to end
                    (repend structure ['dat trim para])
                    |
                    "===" copy para to end
                    (repend structure ['title para])
                    |
                    "---" copy para to end
                    (repend structure ['subhead para])
                    ]
                    if not parsed? [repend structure ['p item]
                    ]
            ]
            structure
        ]

parsedata: func ["Form the structure to HTML content"
           structure /local para
           ][
           parse structure [
                 some [
                      'title set para string!
                      (repend content [newline <p class="head"> para </p>])
                      |
                      'dat set para string!
                      (repend content [newline <p class="subline"> para </p>])
                      |
                      'subhead set para string!
                      (repend content [newline <p class="subhead"> para </p>])
                      |
                      'description set para string!
                      (repend content [newline <p> para </p>])
                      |
                      'p set para string!
                      (repend content [newline <p> para </p>])
                      ]
                ]
]

make-text: func ["Finish the page-content"
           content [string!]
           ][
           page-content: insert content "<text>"
           page-content: append page-content "</text>"
]

;*********** BONUS FUNCTIONS ******************

xml-fix: func [snip] [
	replace/case/all snip "ä" "ae"
	replace/case/all snip "ö" "oe"
	replace/case/all snip "ü" "ue"
	replace/case/all snip "Ä" "Ae"
	replace/case/all snip "Ö" "Oe"
	replace/case/all snip "Ü" "Ue"
	replace/case/all snip "ß" "ss"
	replace/case/all snip "&" "&amp;"      ; checken
	replace/all snip "<br>" "<br/>"
        replace/all snip "<hr>" "<hr/>"
        replace/all snip "'" "&apos;"
        ; replace/all snip "<" "&lt;"
        ; replace/all snip ">" "&gt;"

	return snip
]

emit-xml: function [data] [action tag-word][
    foreach item data [
        action: select [
            word!  [tag-word: form item]
            block! [emit-tag tag-word [emit-xml item]]
        ] type?/word item
        either action [do action] [emit-tag tag-word item]
    ]
]

emit-tag: func [tag value] [
    either block? value [
        emit [indent to-tag tag newline]
        insert/dup indent " " 4
        do value
        remove/part indent 4
        emit [indent to-tag join "/" tag newline]
    ][
        emit [
            indent to-tag tag
            value
            to-tag join "/" tag
            newline
        ]
    ]
]

emit: func [data] [append output reduce data]
output: make string! 8000
indent: make string! 40


sortdata: func [data desc][
           either desc [
              sort/reverse/skip data 2
              ][
              sort/skip data 2
              ]
]

heute: func[ /minus m /plus p /local n][
             n: now
             if minus [n: now - m]
             if plus [n: now + p]
                rejoin [
                either n/day < 10 [
                       join "0" n/day
                       ][
                       n/day
                       ]
                "."
                either n/month < 10 [
                       join "0" n/month
                       ][
                       n/month
                       ]
                "." n/year
                ]
                
]

format-tag: func [string tag /local ][
            inhalt: copy []
            inhalt-temp: copy []
            data: copy string
            data: find data 'title
            inhalt: append inhalt 'title
            inhalt: append inhalt select data 'title
            inhalt: append inhalt 'description

                    while [data: find data tag
                    ][
                    if data/1 = 'p [
                       ; print "FOUND p"
                       inhalt-temp: append inhalt-temp select data tag
                       ; inhalt: append inhalt select data tag
                       ]

                    data: next data

                    if data/2 = 'title [
                       ; print "Found title"
                       inhalt: append/only inhalt form inhalt-temp
                       inhalt-temp: copy []
                       inhalt: append inhalt 'title
                       inhalt: append inhalt select data 'title
                       inhalt: append inhalt 'description
                       ]

                    if (length? data) = 1 [
                       print "READY"
                       inhalt: append/only inhalt form inhalt-temp
                       inhalt-temp: copy []
                       break
                    ]
        ]
        return inhalt
]

build-xml: func [data /local bild link hlink
           ][
           format-tag data 'p

           if verbose [print "Start Parser..."]

           foreach item inhalt [
                   if string? item [

                      replace/all item "</p>" " "
                      replace/all item "<ul>" " "
                      replace/all item "<li>" " "
                      replace/all item "</li>" " "
                      replace/all item "</ul>" " "
                      replace/all item "<strong>" " "
                      replace/all item "</strong>" " "

                      parse/all form item [
                      some [
                           thru "<img" copy bild to ">"
                           (
                           if verbose [print "Found Image..."]
                           replace/all item rejoin ["<img" bild ">"] " ")
                           ]
                        ]

                      parse/all form item [
                      some [
                           to {<a} copy link to {>}
                           1 skip
                           copy hlink to {</a>}
                           (
                           if verbose [print hlink]
                           ahref: rejoin [link ">" hlink {</a>}]
                           replace item ahref hlink
                           )
                           ]
                        ]

                      parse/all form item [
                      some [
                            thru "<p" copy p to ">"
                           (replace item rejoin ["<p" p ">"] " ")
                           ]
                        ]

                   ] ; end if
          ]

          xmldata: copy emit-xml inhalt
          xmldata: xml-fix xmldata

          replace/all xmldata "<title>" "<item><title>"
          replace/all xmldata "</description>" "</description></item>"

          xmldata: append xmldata "</channel></rss>"
          xmldata: insert head xmldata "<?xml version='1.0' encoding='ISO-8859-1'?><rss version='0.91'><channel><title>var-title</title><link>var-link</link><description>var-description</description><language>var-language</language>"

          xmldata: head xmldata
          replace xmldata "var-title" "webcontent 4 all - The REBOL Desktop CMS"
          replace xmldata "var-link" "http://www.webcontent4all.com/weblog.html"
          replace xmldata "var-description" "webcontent 4 all - The free REBOL Desktop CMS Weblog"
          replace xmldata "var-language" "en"

          return xmldata
          ; write %../www/weblog.xml xmldata
]

;************ MAIN LOOP START ******
{
verbose: On
loaddata %../source/weblog/
foreach [datum item] entries [renderdata item]
parsedata structure

if verbose [print rejoin [
                         "Build: entries " length? entries 
                         " structure " length? structure 
                         " content " length? content]
]

; example: build-xml structure
}
; halt

