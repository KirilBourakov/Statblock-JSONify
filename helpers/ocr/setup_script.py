

def setup(venv_dir: str, packages: list):
    try:
        import os
    except:
        print("ERROR, os")
        return
    try:
        import subprocess
    except:
        print("ERROR, subprocess")
        return
    try:
        import sys
    except:
        print("ERROR, sys")
        return
    
    # check if venv exists
    def is_venv_exists(venv_dir):
        return os.path.isdir(venv_dir) and os.path.isdir(os.path.join(venv_dir, 'Scripts' if os.name == 'nt' else 'bin'))

    # check if packages are installed
    def are_packages_installed(venv_dir, required_packages):
        pip_path = os.path.join(venv_dir, 'Scripts', 'pip.exe') if os.name == 'nt' else os.path.join(venv_dir, 'bin', 'pip')
        try:
            result = subprocess.run([pip_path, 'freeze'], capture_output=True, text=True, check=True)
            installed_packages = result.stdout
            return all(pkg in installed_packages for pkg in required_packages)
        except subprocess.CalledProcessError as e:
            print(f"ERROR, checking installed packages: {e}")
            return False
    
    # create venv
    if not is_venv_exists(venv_dir):
        subprocess.check_call([sys.executable, '-m', 'venv', venv_dir])

    # create packages
    if not are_packages_installed(venv_dir, packages):
        pip_path = os.path.join(venv_dir, 'Scripts', 'pip.exe') if os.name == 'nt' else os.path.join(venv_dir, 'bin', 'pip')
        subprocess.check_call([pip_path, 'install'] + packages)

    python_executable = os.path.join(venv_dir, 'Scripts', 'python.exe') if os.name == 'nt' else os.path.join(venv_dir, 'bin', 'python')
    print(python_executable)

if __name__ == "__main__":
    setup("ocr_venv", ["pytesseract", "PIL"])