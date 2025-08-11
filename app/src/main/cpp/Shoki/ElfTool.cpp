/*
    parse elf header from a filepath, returns true, if elf is valid.
    ELF's header must contain:
        Magic:   7f 45 4c 46 02 01 01 00 00 00 00 00 00 00 00 00
        Class:                             ELF64
        Data:                              2's complement, little endian
        Version:                           1 (current)
        OS/ABI:                            UNIX - System V
        Type:                              DYN (Shared object file)
        Machine:                           AArch64
        Version:                           0x1
*/

#include <jni.h>
#include <stdio.h>
#include <elf.h>

extern "C" JNIEXPORT jboolean JNICALL Java_io_kitsuri_mayape_utils_FileParser_ElfCheck(JNIEnv *env, jobject obj, jstring filepath) {

    const char *filepath_cstr = env->GetStringUTFChars(filepath, nullptr);
    if (!filepath_cstr) {
        return JNI_FALSE;
    }


    FILE *fp = fopen(filepath_cstr, "rb");

    env->ReleaseStringUTFChars(filepath, filepath_cstr);
    if (!fp) {
        // File open failed
        return JNI_FALSE;
    }

    // Read the ELF header
    Elf64_Ehdr elf64header;
    if (fread(&elf64header, 1, sizeof(elf64header), fp) != sizeof(elf64header)) {
        // File read failed
        fclose(fp);
        return JNI_FALSE;
    }
    fclose(fp);

    // Validate ELF header
    if (
        // Magic
            elf64header.e_ident[0] == ELFMAG0 &&
            elf64header.e_ident[1] == ELFMAG1 &&
            elf64header.e_ident[2] == ELFMAG2 &&
            elf64header.e_ident[3] == ELFMAG3 &&
            // Bit class
            elf64header.e_ident[4] == ELFCLASS64 &&
            // Data encoding
            elf64header.e_ident[5] == ELFDATA2LSB &&
            // Version
            elf64header.e_ident[6] == EV_CURRENT &&
            // ABI
            elf64header.e_ident[7] == ELFOSABI_SYSV &&
            // Type
            elf64header.e_type == ET_DYN &&
            // Architecture
            elf64header.e_machine == EM_AARCH64
            ) {
        return JNI_TRUE; // ELF is valid
    }
    return JNI_FALSE; // Invalid ELF
}