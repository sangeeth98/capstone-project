#!/usr/bin/env -S uv run
# /// script
# requires-python = ">=3.9"
# dependencies = [
#   "cryptography>=42.0.0",
# ]
# ///
"""
crypt_docs.py

Utility script to encrypt or decrypt confidential documentation files in docs/
using AES-256 (Fernet with PBKDF2 key derivation).

Usage:
    uv run docs/crypt_docs.py encrypt
    uv run docs/crypt_docs.py decrypt
    
    # Or pass key via environment variable:
    # DOCS_PASSPHRASE="your secret key" uv run docs/crypt_docs.py decrypt
"""

import argparse
import base64
import getpass
import os
import sys
from pathlib import Path

from cryptography.fernet import Fernet
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

SALT = b"sentinelai_confidential_salt_2026"


def derive_key(passphrase: bytes, salt: bytes) -> bytes:
    """Derives a 32-byte Fernet key from a user passphrase using PBKDF2HMAC."""
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=480000,
    )
    return base64.urlsafe_b64encode(kdf.derive(passphrase))


def encrypt_file(file_path: Path, cipher: Fernet):
    """Encrypts a single file and appends .enc extension."""
    print(f"Encrypting: {file_path}")
    data = file_path.read_bytes()
    encrypted_data = cipher.encrypt(data)
    enc_path = file_path.with_suffix(file_path.suffix + ".enc")
    enc_path.write_bytes(encrypted_data)
    file_path.unlink()  # Remove original unencrypted file


def decrypt_file(enc_path: Path, cipher: Fernet):
    """Decrypts a .enc file and restores original file extension."""
    print(f"Decrypting: {enc_path}")
    encrypted_data = enc_path.read_bytes()
    decrypted_data = cipher.decrypt(encrypted_data)
    orig_path = enc_path.with_suffix("")  # Strip .enc
    orig_path.write_bytes(decrypted_data)
    enc_path.unlink()  # Remove encrypted file


def parse_args():
    parser = argparse.ArgumentParser(
        description="Encrypt or decrypt confidential documentation"
    )
    parser.add_argument(
        "action",
        choices=["encrypt", "decrypt"],
        help="Action to perform: 'encrypt' or 'decrypt'",
    )
    parser.add_argument(
        "-k",
        "--key",
        help="Passphrase for encryption/decryption (if omitted, will prompt securely)",
    )
    return parser.parse_args()


def main():
    args = parse_args()

    # Get passphrase from CLI arg, environment variable, or interactive prompt
    passphrase_str = args.key or os.getenv("DOCS_PASSPHRASE")
    if not passphrase_str:
        prompt_msg = (
            "Enter encryption passphrase: "
            if args.action == "encrypt"
            else "Enter decryption passphrase: "
        )
        passphrase_str = getpass.getpass(prompt_msg)

    if not passphrase_str:
        print("Error: Passphrase cannot be empty.", file=sys.stderr)
        sys.exit(1)

    key = derive_key(passphrase_str.encode("utf-8"), SALT)
    cipher = Fernet(key)

    docs_dir = Path(__file__).parent

    if args.action == "encrypt":
        target_extensions = [".pdf", ".ppt", ".pptx"]
        files_to_encrypt = []
        for ext in target_extensions:
            files_to_encrypt.extend(docs_dir.rglob(f"*{ext}"))

        files_to_process = [
            f for f in files_to_encrypt if not f.name.endswith(".enc")
        ]
        if not files_to_process:
            print("No unencrypted PDF or PPT files found to encrypt.")
            return

        for f in files_to_process:
            encrypt_file(f, cipher)
        print(f"\nSuccessfully encrypted {len(files_to_process)} files.")

    elif args.action == "decrypt":
        enc_files = list(docs_dir.rglob("*.enc"))
        if not enc_files:
            print("No .enc files found to decrypt.")
            return

        for f in enc_files:
            decrypt_file(f, cipher)
        print(f"\nSuccessfully decrypted {len(enc_files)} files.")


if __name__ == "__main__":
    main()
