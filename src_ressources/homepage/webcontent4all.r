REBOL [
    Title: "Webcontent4all"
    Date: 09-09-2004
    File: %webcontent4all.r
    Author: ["Thomas Beeskow"]
    Purpose: {Based on the great idea of Carl Sassenrath - Web Site Builder,
    WebContent4all is a modular content system and transform raw documents like
    txt, xml or html to a complete website with navigation.}
    Version: 1.0.6
    History: [
    1.0.1    {Supports more source filetypes, new structure, external libraries}
    1.0.2    {More markers at the template: menu, content, footer. Different templates}
    1.0.3    {Upload of images, javascripts and css}
    1.0.4    {news functions, auto-menu and extra column}
    1.0.5    {XML RSS 0.91 Export, sorting news, Menu On/Off}
    1.0.6    {bugfixes and FTP Download of new files only}
    1.0.7    {renamed news to news}
    ]
]

;****** Startup Default ********************************************************
verbose: on                     ; print more info
menu: on                        ; enable Menu-Maker function
auto-expand: on                 ; auto expanding menus
ftp-passive: on                 ; ON if behind a router or DSL

upload: off                     ; enables ftp://
ftp-host: "www.meineseite.de"   ; upload path to webserver

updated-files: []               ; filelist
updated-image-files: []         ; binary-filelist

source-dir: %source/            ; where to find your content files
image-dir: %images/             ; put here all your .gif and .jpg files
Other-dir: [                    ; use this for other folders
           %styles/
           ]

temp-dir: %templates/           ; where to find the different templates
templates: [
            %index.html         ; template for About page (menu entry "About")
            %news.html          ; template for news page (menu entry "news")
            %standard.html      ; template for the rest
            ]

markers: [
            menu-area           ; must be unique words in the template
            content-area
            content-extra
            footer-area
            ]

output-dir: %htdocs/            ; local path to finished files

intranet: off                   ; use this for your local server
intranet-dir: %test/            ; path to your local folder

lib-dir: %lib/                  ; where to find helper functions etc.
libaries: [
            %utility.r          ; some helper functions (error, message, xhtml]
            %texthtml.r         ; renders .txt to .html
            %dir-tree.r
            %newsXML.r          ; Build a news plus RSS
            ]

news: on                        ; enables news-function
news-dir: %source/news/         ; local path to your news entries

; load the website structure
menu: load to-file %menu/menu.txt

; <fg> commented out automatic menu entry for news
;if news [
;   append tail menu ["news" %news.html none]
;]

other-files: load to-file %menu/other-files.txt
time-stamp: either exists? %timestamp.r [load %timestamp.r][1-1-1900]

; load internal lib
foreach word libaries [
        do load to-file Lib-Dir/:word
        if verbose [message ["Load Library" word]]
        ]

;****** HTML Template *********************************************************
template-chooser: func [n /local t][
        t: copy to-file pick templates n
           if not exists? temp-dir/:t [error ["Template: " t "unknown"]]
        Template: read Temp-Dir/:t
        item: template

]      ; end template-chooser

;****** Builder Functions *****************************************************

make-page: func [
    "Make the new web page with menus."
    file section title
    /local contents page masterpage
][
    if not find "news" section [
       if not exists? source-dir/:file [error ["Missing source file:" file]]

       if all [
              time-stamp > modified? %webcontent4all.r
              time-stamp > modified? source-dir/:file
              ]
       [
        if verbose [message ["No Update"]]
       exit
       ]
    ]

    if verbose [message ["Building:" file]]

    if find "other.txt" file [
       if verbose [message ["Found:" file]]
       contents: text-to-html/convert read source-dir/:file
       xhtml-fix contents
       ; message contents
       save-page file contents
       exit
       ]
       
;------ Footer Loader ---------------------------------------------------
    footer: read source-dir/footer.html
    xhtml-fix footer

    ; masterpage: head insert at copy template footer-area footer
    masterpage: replace copy template "footer-area" footer

;------ Content Loader --------------------------------------------------
    either find "news" section [

           loaddata news-dir
           foreach [datum item] entries [renderdata item]
           parsedata structure
           make-text to-string content

           contents: copy page-content

           build-xml structure
           write output-dir/news.xml xmldata
           ][
           contents: read source-dir/:file        ; load content
    ]

    xhtml-fix contents                           ; Sonderzeichen

    if not parse contents [to "<text" thru ">" copy content-area to </text> to end
    ][
    content-area: copy contents
    ]
    if not parse contents [to "<column" thru ">" copy content-extra to </column> to end][
    content-extra: read source-dir/column.txt
    ]

    page: replace masterpage "content-area" content-area
    page: replace page "content-extra" content-extra

    if menu [make-menu page section title]        ; create the menu
    insert find page </title> join " " title      ; create title
    save-page file page
    append updated-files file
]

save-page: func [
    "Saves the page at the output directory"
     file page
     ][
     system/options/quiet: true
     clear change find/tail file "." "html"       ; makes every file *.html
     write output-dir/:file page
]

make-menu: func [
    "Make the approriate menu for a page."
    page section title
    /local menu-part
][
    menu-part: find page "menu-area"
    menu-part: insert menu-part <ul id="popMenu">
    menu-part: insert menu-part newline
    foreach [menu-item file sub-menu] menu [
        menu-part: link-menu menu-part file menu-item false true

        if any [not auto-expand menu-item = section] [  ; we are in this section
            if sub-menu <> 'none [
                menu-part: insert menu-part <ul>
                menu-part: insert menu-part newline
                foreach [titl file] sub-menu [
                    menu-part: link-menu menu-part file titl true titl = title
                ]
                menu-part: insert menu-part </ul>
                menu-part: insert menu-part newline
            ]
           menu-part: insert menu-part </li>
           menu-part: insert menu-part newline
        ]
    ]
    menu-part: insert menu-part </ul>
    replace page "menu-area" ""
]

link-menu: func [
    "Create a linked menu item."
    menu-tail file text sub current
    /local filename xxl li
    ][     ; alle Einträge auf *.html
           filename: copy file
           clear change find/tail filename "." "html"
           ; nicht mehr als 10 Zeichen für Titel = xxl true | false?
           xxl: (length? text) > 15
           xxl-li: either xxl [<li style="width: 210px;">][<li>]

    insert menu-tail reduce either current [[  ; current = true
        either sub [<li class="sub"> ][xxl-li]
        {<a href="} form filename {">} text </a>
        newline
    ]][[                                           ; current = false
        either sub [<li class="sub"> ][xxl-li]
        {<a href="} form filename {">} text </a></li>
        newline
    ]]
]

;****** Main Loop *************************************************************

foreach [section file sub-menu] menu [
        ; look for ABOUT in menu and switch the right template
        ;either find "About" section [template-chooser 1][template-chooser 2]
        ;either find "news" section [template-chooser 2][template-chooser 3]

        message ["Building MENU" section]

        template-chooser 3

        if find "About" section [
          template-chooser 1
          message ["TEMPLATE: About"]
        ]
        if find "News" section [
          template-chooser 2
          message ["TEMPLATE: News"]
        ]

        make-page file section section

    if block? sub-menu [
        foreach [title file] sub-menu [make-page file section title]
    ]
]

foreach file other-files [
        template-chooser 2 ; possible to switch to a third template
        make-page file none none
]

; check and copy images
foreach file read source-dir/:image-dir[
        if verbose [message ["Found" file]]

        if not exists? output-dir/:image-dir[
            make-dir output-dir/:image-dir
        ]
        
       if time-stamp < modified? source-dir/:image-dir/:file
       	  [
	  if verbose [message ["...>" file "upload"]]
          write/binary output-dir/:image-dir/:file read/binary source-dir/:image-dir/:file
	  append updated-image-files file
	]
]

; check and copy *.ico and *.htc
foreach file read source-dir [
        if find [%.htc %.ico %.zip %.rar %.sit] suffix? file [
           if verbose [message ["Found..." file]]
           
           if time-stamp < modified? source-dir/:file
       	      [
	      if verbose [message ["...>" file "upload"]]
              write/binary output-dir/:file read/binary source-dir/:file
              append updated-files file
              ]
        ]
]

foreach item other-dir [
        foreach file read source-dir/:item [
                if verbose [message ["Found" file]]
                if not exists? output-dir/:item [
                   make-dir output-dir/:item
                ]
                
           	if time-stamp < modified? source-dir/:item/:file
       	      	   [
	      	   if verbose [message ["...>" file "upload"]]

                write output-dir/:item/:file read source-dir/:item/:file
                append updated-files item/:file
                ]
        ]
]

;****** Upload Files **********************************************************

if upload [
    if find/match ask "Upload now? Type y + ENTER " "y" [
    message "Uploading..."
    either exists? %userpass.r [do %userpass.r][
        user: ask "Username? "
        pass: ask "Password? "
    ]
    copy-dir output-dir join ftp:// [user ":" pass "@" ftp-host "/"] "ftp"
    save %timestamp.r now
    ]
]

if intranet [copy-dir output-dir intranet-dir "intranet"]

write %update.r remold ["files" updated-files "images" updated-image-files]

if find/match quit [quit]
halt
