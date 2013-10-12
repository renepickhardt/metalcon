fh = open("metalcon-nonUser.csv")

def java_string_hashcode(s):
    h = 0
    for c in s:
        h = (31 * h + ord(c)) & 0xFFFFFFFF
    return ((h + 0x80000000) & 0xFFFFFFFF) - 0x80000000

for line in fh:
    head, tail = line.split("\t")
    if not tail.isdigit():
        tail = str(java_string_hashcode(tail))
    print head + "\t" + tail
