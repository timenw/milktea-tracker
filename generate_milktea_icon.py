#!/usr/bin/env python3
"""Generate milk tea app icon for '奶茶了么'."""
import struct
import zlib
import math

def create_png(width, height, color_fn):
    raw = b''
    for y in range(height):
        raw += b'\x00'
        for x in range(width):
            r, g, b, a = color_fn(x, y, width, height)
            raw += bytes([r, g, b, a])
    def chunk(ctype, data):
        c = ctype + data
        return struct.pack('>I', len(data)) + c + struct.pack('>I', zlib.crc32(c) & 0xffffffff)
    png = b'\x89PNG\r\n\x1a\n'
    png += chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0))
    png += chunk(b'IDAT', zlib.compress(raw))
    png += chunk(b'IEND', b'')
    return png

def milktea_icon(x, y, w, h):
    cx, cy = w // 2, h // 2
    border = 4
    corner_r = border + 2
    corners = [(border, border), (w-border, border), (border, h-border), (w-border, h-border)]
    for corner in corners:
        if math.sqrt((x - corner[0])**2 + (y - corner[1])**2) > corner_r:
            if (x < border and y < border) or (x >= w-border and y < border) or (x < border and y >= h-border) or (x >= w-border and y >= h-border):
                return (0, 0, 0, 0)
    dist = math.sqrt((x - cx)**2 + (y - cy)**2)
    max_dist = math.sqrt(cx**2 + cy**2)
    t = dist / max_dist
    bg_r = int(60 - t * 25)
    bg_g = int(40 - t * 15)
    bg_b = int(25 - t * 10)

    # Cup shape
    cup_top = int(h * 0.2)
    cup_bottom = int(h * 0.7)
    cup_left = int(w * 0.25)
    cup_right = int(w * 0.75)
    cup_progress = (y - cup_top) / max(1, cup_bottom - cup_top)
    cup_wider = 0.8 + 0.2 * math.sin(cup_progress * math.pi)
    current_left = int(cup_left + (cup_right - cup_left) * (1 - cup_wider) / 2)
    current_right = int(cup_right - (cup_right - cup_left) * (1 - cup_wider) / 2)

    if current_left <= x <= current_right and cup_top <= y <= cup_bottom:
        cup_t = (x - current_left) / max(1, current_right - current_left)
        highlight = max(0, 1 - abs(cup_t - 0.3) * 2) * 0.3
        # Milk tea color: creamy brown
        r = min(255, int(200 * (0.7 + highlight)))
        g = min(255, int(170 * (0.7 + highlight)))
        b = min(255, int(130 * (0.7 + highlight)))
        # Outline
        if x - current_left < 2 or current_right - x < 2 or y - cup_top < 2:
            r, g, b = int(r * 0.5), int(g * 0.45), int(b * 0.4)
        # Liquid inside
        liquid_top = int(cup_top + (cup_bottom - cup_top) * 0.1)
        if y > liquid_top and y < cup_bottom - 3:
            liquid_t = (y - liquid_top) / max(1, cup_bottom - liquid_top)
            r = int(r * 0.7 + 30 * (1 - liquid_t))
            g = int(g * 0.6 + 40 * (1 - liquid_t))
            b = int(b * 0.5 + 20 * (1 - liquid_t))
        return (r, g, b, 255)

    # Straw
    straw_x = int(w * 0.55)
    straw_top = int(h * 0.08)
    straw_bottom = int(h * 0.35)
    if abs(x - straw_x) < 3 and straw_top <= y <= straw_bottom:
        return (255, 180, 180, 255)

    # Boba pearls at bottom
    boba_y = int(h * 0.62)
    for bx in range(current_left + 8, current_right - 8, 10):
        boba_dist = math.sqrt((x - bx)**2 + (y - boba_y)**2)
        if boba_dist < 4:
            shade = 0.4 + (bx % 3) * 0.1
            return (int(60 * shade), int(40 * shade), int(25 * shade), 255)

    # Steam
    for i, offset in enumerate([-8, 8]):
        steam_cx = cx + offset
        steam_phase = (y - int(h * 0.05)) / max(1, int(h * 0.12))
        if 0 < steam_phase < 1:
            wave = math.sin(steam_phase * math.pi * 2 + i * 1.5) * 5
            steam_x = int(steam_cx + wave)
            if abs(x - steam_x) < 2 and y < cup_top:
                alpha = int(60 * (1 - steam_phase))
                return (255, 255, 255, alpha)

    # Small heart (love for milk tea)
    heart_cx = int(w * 0.8)
    heart_cy = int(h * 0.2)
    heart_r = int(w * 0.06)
    hx = (x - heart_cx) / max(1, heart_r)
    hy = (heart_cy - y) / max(1, heart_r) + 0.3
    heart_val = (hx*hx + hy*hy - 1)**3 - hx*hx * hy*hy*hy
    if heart_val < 0:
        return (255, 120, 150, 200)

    return (bg_r, bg_g, bg_b, 255)

sizes = {'mdpi': 48, 'hdpi': 72, 'xhdpi': 96, 'xxhdpi': 144, 'xxxhdpi': 192}
output_dir = '/root/milktea-tracker/android/app/src/main/res'
import os

for density, size in sizes.items():
    png_data = create_png(size, size, milktea_icon)
    path = os.path.join(output_dir, f'mipmap-{density}', 'ic_launcher.png')
    with open(path, 'wb') as f:
        f.write(png_data)
    print(f'Created {path} ({size}x{size}, {len(png_data)} bytes)')
    path_round = os.path.join(output_dir, f'mipmap-{density}', 'ic_launcher_round.png')
    with open(path_round, 'wb') as f:
        f.write(png_data)

print('\nAll milk tea icons generated!')
