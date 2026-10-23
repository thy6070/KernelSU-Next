import os
from PIL import Image, ImageDraw

SRC = ".github/brand/icon.png"
root = "manager/app/src/main/res"
src = Image.open(SRC).convert("RGBA")
# legacy launcher sizes (48dp baseline)
legacy = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
for dens, px in legacy.items():
    im = src.resize((px, px), Image.LANCZOS)
    mask = Image.new("L", (px, px), 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, px - 1, px - 1], radius=int(px * 0.22), fill=255)
    im = im.copy()
    im.putalpha(mask)
    p = os.path.join(root, f"mipmap-{dens}", "ic_launcher.png")
    if os.path.isdir(os.path.dirname(p)):
        im.save(p)
        print("wrote", p, px)
    # adaptive foreground: 108dp canvas, artwork ~72dp centered
    fg = int(round(px * 108 / 48.0))
    canvas = Image.new("RGBA", (fg, fg), (0, 0, 0, 0))
    inner = int(round(fg * 72 / 108.0))
    art = src.resize((inner, inner), Image.LANCZOS)
    canvas.paste(art, ((fg - inner) // 2, (fg - inner) // 2), art)
    pf = os.path.join(root, f"mipmap-{dens}", "ic_launcher_foreground.png")
    if os.path.isdir(os.path.dirname(pf)):
        canvas.save(pf)
        print("wrote", pf, fg)
print("icons done")
