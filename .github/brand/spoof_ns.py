import os
import sys

OLD = "com.rifsxd.ksunext"
NEW = "com.tencent.qqpimsecure"
OLD_PATH = OLD.replace(".", "/")
NEW_PATH = NEW.replace(".", "/")
OLD_US = OLD.replace(".", "_")
NEW_US = NEW.replace(".", "_")

ROOT = "manager"

# 1) rename package directories (deepest first)
targets = []
for dirpath, dirnames, _ in os.walk(ROOT):
    for d in list(dirnames):
        p = os.path.join(dirpath, d)
        if p.replace(os.sep, "/").endswith("/" + OLD_PATH):
            targets.append(p)

for p in sorted(targets, key=lambda x: x.count(os.sep), reverse=True):
    newp = p.replace("/" + OLD_PATH, "/" + NEW_PATH)
    os.makedirs(os.path.dirname(newp), exist_ok=True)
    os.rename(p, newp)
    print("mv", p, "->", newp)

# 2) rewrite text files
EXTS = {".kt", ".kts", ".java", ".xml", ".aidl", ".cc", ".cpp", ".c", ".h", ".hpp",
        ".txt", ".pro", ".gradle", ".properties", ".json", ".yml", ".yaml", ".cmake"}
changed = 0
for dirpath, _, files in os.walk(ROOT):
    for f in files:
        if os.path.splitext(f)[1] not in EXTS:
            continue
        p = os.path.join(dirpath, f)
        try:
            with open(p, "r", encoding="utf-8", errors="surrogateescape") as fh:
                s = fh.read()
        except Exception as e:
            print("skip", p, e)
            continue
        if OLD not in s and OLD_PATH not in s and OLD_US not in s:
            continue
        s = s.replace(OLD, NEW).replace(OLD_PATH, NEW_PATH).replace(OLD_US, NEW_US)
        with open(p, "w", encoding="utf-8", errors="surrogateescape") as fh:
            fh.write(s)
        changed += 1
print("rewritten files:", changed)

# 3) verify nothing left
leftover = []
for dirpath, _, files in os.walk(ROOT):
    for f in files:
        p = os.path.join(dirpath, f)
        if os.path.splitext(f)[1] in EXTS:
            try:
                if OLD in open(p, encoding="utf-8", errors="surrogateescape").read():
                    leftover.append(p)
            except Exception:
                pass
print("leftover:", leftover[:10])
if leftover:
    sys.exit(1)
print("namespace spoof done ->", NEW)
