rebol []
;****** Utility Functions *****************************************************

error: func [msg] [print msg halt]
message: func [msg] [print msg]

if ftp-passive [
   system/schemes/ftp/passive: true       ; true if behind a router/DSL
   ]

xhtml-fix: func [snip] [
	replace/case/all snip "ä" "&auml;"
	replace/case/all snip "ö" "&ouml;"
	replace/case/all snip "ü" "&uuml;"
	replace/case/all snip "Ä" "&Auml;"
	replace/case/all snip "Ö" "&Ouml;"
	replace/case/all snip "Ü" "&Uuml;"
	replace/case/all snip "ß" "&szlig;"

	replace/all snip "<br>" "<br/>"
        replace/all snip "<hr>" "<hr/>"
	return snip
]

Push: func [
    "Inserts a value into a series and returns the series head."
    Stack [series! port! bitset!]   "Series at point to insert."
    Value [any-type!] /Only "The value to insert."
    ][
    head either Only [
        insert/only Stack :Value
        ][
        insert Stack :Value
        ]
    ]

; example: push [a b c] "a fox jumps"

Pop: function [
    "Returns the first value in a series and removes it from the series."
    Stack [series! port! bitset!]   "Series at point to pop from."
    ][
    Value
    ][
    Value: pick Stack 1
    remove Stack
    :Value
    ]

;----- for columns
wrap-text: func [
    para
    /margin size "Char count after which the wrap occurs."
    /local count
][
    count: 1
    if not margin [size: 50] ; default size
    trim/lines para
    forall para [
    if all [count >= size find/match para " "][
        change para newline
        count: 0
    ]
    count: count + 1
    ]
    head para
]

;example: print wrap-text/margin {textbeispiele...} 30

copy-dir: func [
        "create a copy of a complete directory"
        source dest meldung
        ][
        if not exists? dest [make-dir/deep dest]
        foreach file read source [
            either find file "/" [
                copy-dir source/:file dest/:file none
            ][
                if meldung = "ftp" [message ["Upload" file]]
                if meldung = "intranet" [message ["Copy:" dest/:file]]

                write/binary dest/:file read/binary source/:file
            ]
        ]
]

;*********** READ Directory below **********
read-below: func [
    {Read all directories below and including a given file path.}
    [catch throw]
    path [file! url!] "Must be a directory (ending in a trailing slash)."
    /foreach "Evaluates a block for each file or directory found."
    'word [word!] "Word set to each file or directory."
    body [block!] "Block to evaluate for each file or directory."
    /local queue file-list result file do-func
] [

    if #"/" <> last path [
        throw make error! "read-below expected path to have trailing slash."
    ]

    ; Initialise parameters
    if not foreach [
        word: 'file
        file-list: make block! 10000
        body: [insert tail file-list file]
    ]

    ; Create process function
    do-func: func reduce [[throw] word] body

    ; Initialise queue
    queue: append make list! 10 read path

    ; Process queue
    set/any 'result if not empty? queue [
        until [
            do-func file: first queue
            queue: remove queue
            if #"/" = last file [
                repeat f read join path file [insert queue join file f]
                queue: head queue
            ]
            tail? queue
        ]
    ]

    ; Return result.
    if not foreach [result: file-list]
    get/any 'result
]

iso-date: func [
	reb-date [ date! ] "Das aktuelle Datum im REBOL-Format."
] [
	reb-time: reb-date/time
	year: to-string reb-date/year
	month: to-string reb-date/month
	day: to-string reb-date/day
	hour: to-string reb-time/hour
	minute: to-string reb-time/minute
	seconds: to-string reb-time/second

	if (length? month) < 2 [ insert month "0" ]
	if (length? day) < 2 [ insert day "0" ]
	if (length? hour) < 2 [ insert hour "0" ]
	if (length? minute) < 2 [ insert minute "0" ]
	if (length? seconds) < 2 [ insert seconds "0" ]

	fulldate: join year [ "-" month "-" day "T" hour ":" minute ":" seconds ]

	make object! compose [
		fulldate: (fulldate)
		year: (year)
		month: (month)
		day: (day)
		hour: (hour)
		minute: (minute)
		second: (seconds)
	]
]

; jetzt: iso-date now
; jetzt/fulldate

Weekday?: func[Date[date!]][pick system/locale/days Date/weekday]
; weekday? now
month?: func[Date[date!]][pick system/locale/months Date/month]
; month? 18/07/2004


protect-email: func [a /local u d][
	u: d: none
	either parse a [copy u to "@" skip copy d to end] [
		;u: hex-html u
		;d: hex-html d
		a: rejoin [{<script type="text/javascript"><!--
u="} u {";h="} d {";e=(u + "&#64;" + h);document.write("<a href='mai" + "lto&#58;" + e + "'>" + e + "</a>"); // --></script>}]
		][""]
	]

link-check: func [file][
parse read file [any [thru "A HREF=" copy link to ">" (print link)] to end]
]

