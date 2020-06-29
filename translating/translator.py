import sys
import os

modid = "infinity"
modname = "Infinity Item Editor"
dirname = os.path.dirname(__file__)
lang = os.path.join(dirname, f'../src/main/resources/assets/{modid}/lang/')
files = sorted([file for file in os.listdir(lang) if file.endswith(".lang")])
commands = ["list","sort","compare","show"]

def main():
	args = sys.argv
	if len(args)==1:
		print("No arguments found, use one of:")
		print(*commands,sep='\n')

	elif len(args)==2:
		c = args[1]
		if c not in commands:
			print("Didn't recognize that argument. Use one of:")
			print(*commands,sep='\n')

		elif c == "list":
			print("The list of existing localization files are:")
			print(*files,sep='\n')

		elif c == "sort":
			print("Need a file to sort:")
			print("sort <file>")

		elif c == "compare":
			print("Need a file to compare:")
			print("compare <file>")

		elif c == "show":
			print("Need a file to show:")
			print("show <file>")

	elif len(args)==3:
		c = args[1]
		a = args[2]

		if c not in commands:
			print("Didn't recognize that argument. Use one of:")
			print(*commands,sep='\n')

		elif c=="sort" and a in files:
			sort(a)

		elif c=="compare" and a in files:
			compare(a)

		else:
			print("Too many arguments")


def sort(file):
	with open(os.path.join(lang, file),'r', encoding="utf8") as f:
		lines = f.readlines()
		lines.sort()

	with open(os.path.join(lang, file),'w', encoding="utf8") as f:
		for line in lines:
			f.write(line)

def compare(file):
	if file == "en_us.lang":
		print("Can't compare en_us.lang")
		return

	with open(os.path.join(lang, "en_us.lang"),'r', encoding="utf8") as f:
		us_lines = f.readlines()
		us_lines = [i[:i.index('=')] for i in us_lines if '=' in i]

	with open(os.path.join(lang, file),'r', encoding="utf8") as f:
		file_lines = f.readlines()
		file_lines = [i[:i.index('=')] for i in file_lines if '=' in i]

	for i in us_lines:
		if i not in file_lines:
			print(i)

if __name__ == '__main__':
	main()
