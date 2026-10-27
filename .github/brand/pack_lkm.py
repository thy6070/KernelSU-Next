import os
import sys

KEY = bytes.fromhex("a71f3c9e5b2d8460f9c1e7a3d5b8204613cf9e7a5b2d8461f0e3c9a7b5d24618")


def xor(data: bytes) -> bytes:
    n = len(KEY)
    return bytes(b ^ KEY[i % n] for i, b in enumerate(data))


src = sys.argv[1]
dst = sys.argv[2]
os.makedirs(dst, exist_ok=True)
count = 0
for root, _dirs, files in os.walk(src):
    for name in files:
        if not name.endswith(".ko"):
            continue
        raw = open(os.path.join(root, name), "rb").read()
        out = os.path.join(dst, name + ".bin")
        with open(out, "wb") as fh:
            fh.write(xor(raw))
        count += 1
print("packed", count, "modules into", dst)
