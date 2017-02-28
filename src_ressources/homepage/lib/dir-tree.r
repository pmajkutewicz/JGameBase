rebol[]

;****** Directory Lister ********************************************
;   Example: dir-tree/depth %/c/inetpub/wwwroot/ 2

dir-tree: func [
    current-path [file! url!] "directory to explore"
    /inner
    tree [block!] "useful to avoid stack overflow"
    /depth "recursion depth, 1 for current level, -1 for infinite"
    depth-arg [integer!]
    /local
    current-list
    sub-tree
    item
][
    if all [not inner not block? tree] [tree: copy []]
    depth-arg: either all [depth integer? depth-arg] [depth-arg - 1][-1]
    current-list: read current-path
    if not none? current-list [
        foreach item current-list [
            insert tail tree item
            if all [dir? current-path/:item not-equal? depth-arg 0] [
                sub-tree: copy []
                dir-tree/inner/depth current-path/:item sub-tree depth-arg
                insert/only tail tree sub-tree
            ]
        ]
    ]
    return tree
]

;halt ; to terminate script if DO'ne from webpage
