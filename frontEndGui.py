from cgitb import text
from tkinter import *
import tkinter as tkinter
from PIL import ImageTk, Image
from tkinter import filedialog

root = Tk()
root.title('ML Image Recoginzer')
image_display_frame = LabelFrame(root, padx=50, pady=50)
image_display_frame.grid(row=0, column=0, columnspan=2)


root.imageName = "imageDir/blank_filler.png"
root.selected_image = ImageTk.PhotoImage(Image.open(root.imageName).resize(
    (600, 400)))  # 600*400 is the size of canvas board of JavaCanvas
Label(image_display_frame, image=root.selected_image).pack()


def open():
    root.imageName = filedialog.askopenfilename(initialdir="imageDir/", title="Select A File", filetypes=(
        ("png files", "*.png"), ("jpg files", "*.jpg"), ("all files", "*.*")))
    root.selected_image = ImageTk.PhotoImage(Image.open(root.imageName).resize((600,400)))
    # cleaning existing image in the frame https://stackoverflow.com/a/28623781
    for widget in image_display_frame.winfo_children():
        widget.destroy()
    if root.selected_image:
        selected_image_label = Label(
            image_display_frame, image=root.selected_image).pack()


def ML_recognizer():
    global text_field
    text_field.insert(
        tkinter.END, "Machine learning algorithm not yet implemented, still in the phase of scoping, this is a place holder for the result!")


image_btn = Button(root, text="Open File", command=open).grid(row=1, column=0)
recognizer_btn = Button(root, text="Recoginze Picture",
                        command=ML_recognizer).grid(row=1, column=1)
text_field_title = Label(root, text="Result Box").grid(row=2, column=0, pady=1)
text_field = Text(root, height=5, width=40)
text_field.grid(
    row=3, column=0, columnspan=2, pady=2)


root.mainloop()
