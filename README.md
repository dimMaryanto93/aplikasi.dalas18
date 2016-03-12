## Sistem Informasi Pemasukan dan Pengeluaran Kas

Sistem Informasi ini berfokus pada pencatatan transaksi yang berkaitan dengan pemasukan dan pengeluaran kas pada UKM Es Dalas.
berikut ini adalah batasan sistem yang terdiri dari :

* Penjualan (es krim), input jumlah dan harganya sesuia dengan buku catatan harian penjualan.
* Pembelian bahan baku (Gula dan hanco)
* Pengiriman penjualan (dikirim sendiri)
* Biaya oprasional
* Laporan keuangan (Akutansi sederhana)

## Spesial thanks to

1. **bu. Lusi Melian, M.T (pembimbing)**
2. **bpk. Bella Hardiyana (penguji)**
3. **bu. Diana Effendi (penguji)**

## Download Draft SKRIPSI.pdf

[Download draft pdf](https://github.com/dimMaryanto/dalas18-cash-management/releases)

## Lihat di youtube (demo unstable version)

[![play in youtube](http://img.youtube.com/vi/w2QCogdllio/default.jpg)](https://www.youtube.com/watch?v=w2QCogdllio)

## System required (untuk menggunakan aplikasi)

* Oracle jre-1.8
* PostgreSQL 9.4
* Apache Maven

## System Support (untuk menjalankan aplikasi)

### Spesifkasi minimum

* CPU intel atom dual core
* RAM 2gb
* HDD 20mb
* Resolusi layar 1200x720 

### Spesifikasi yang disarankan

* CPU intel core i3
* RAM 4gb
* HDD 250gb
* Resolusi layar 1366x768 (14 inci)
 
## Installasi software

* clone repository

```
git clone https://github.com/dimMaryanto/dalas18-cash-management.git
```

* export ke jar

```
mvn clean compile assembly:single
```

* execute jar file

```
java -jar target/dalas18-cash-management-1.0-jar-with-dependencies.jar
```
