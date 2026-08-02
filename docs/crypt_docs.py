import sys
import os
import glob
from pathlib import Path
import base64
from cryptography.fernet import Fernet
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

PASSPHRASE = b"i solemnly swear that i am up to no good"
SALT = b"sentinelai_confidential_salt_2026"

def derive_key(passphrase: bytes, salt: bytes) -> bytes:
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=480000,
    )
    return base64.urlsafe_b64encode(kdf.derive(passphrase))

def encrypt_file(file_path: Path, cipher: Fernet):
    print(f"Encrypting: {file_path}")
    data = file_path.read_bytes()
    encrypted_data = cipher.encrypt(data)
    enc_path = file_path.with_suffix(file_path.suffix + ".enc")
    enc_path.write_bytes(encrypted_data)
    file_path.unlink() # Delete original plaintext file

def decrypt_file(enc_path: Path, cipher: Fernet):
    print(f"Decrypting: {enc_path}")
    encrypted_data = enc_path.read_bytes()
    decrypted_data = cipher.decrypt(encrypted_data)
    orig_path = enc_path.with_suffix("") # Strip .enc
    orig_path.write_bytes(decrypted_data)
    enc_path.unlink() # Delete encrypted file

def main():
    mode = sys.argv[1] if len(sys.argv) > 1 else "encrypt"
    key = derive_key(PASSPHRASE, SALT)
    cipher = Fernet(key)

    docs_dir = Path("docs")

    if mode == "encrypt":
        target_extensions = [".pdf", ".ppt", ".pptx"]
        files_to_encrypt = []
        for ext in target_extensions:
            files_to_encrypt.extend(docs_dir.rglob(f"*{ext}"))
        
        for f in files_to_encrypt:
            if not f.name.endswith(".enc"):
                encrypt_file(f, cipher)
        print(f"Successfully encrypted {len(files_to_encrypt)} files.")

    elif mode == "decrypt":
        enc_files = list(docs_dir.rglob("*.enc"))
        for f in enc_files:
            decrypt_file(f, cipher)
        print(f"Successfully decrypted {len(enc_files)} files.")

if __name__ == "__main__":
    main()
