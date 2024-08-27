

def main():
    try:
        import sys
    except:
        print("ERROR, sys")
    try:
        import pytesseract
    except:
        print("ERROR, pytesseract")

    if (len(sys.argv) != 2):
        print("ERROR, args")
        return

    image = open_and_enhance_image(sys.argv[1])
    text = pytesseract.image_to_string(image, lang='eng')
    print(text)
    

def open_and_enhance_image(path: str):
    try:
        import PIL
    except:
        print("ERROR, PIL")

    image = PIL.Image.open(path)

    gray_image = PIL.ImageOps.grayscale(image)
    threshold_image = PIL.ImageOps.autocontrast(gray_image, cutoff=0)
    threshold_image = PIL.ImageOps.invert(threshold_image)

    enhancer = PIL.ImageEnhance.Contrast(threshold_image)
    enhanced_image = enhancer.enhance(1.5)

    new_size = (enhanced_image.width * 4, enhanced_image.height * 4) 
    resized_image = enhanced_image.resize(new_size, PIL.Image.Resampling.LANCZOS)

    inverted_image = PIL.ImageOps.invert(resized_image)
    return inverted_image



if __name__ == 'main':
    main()