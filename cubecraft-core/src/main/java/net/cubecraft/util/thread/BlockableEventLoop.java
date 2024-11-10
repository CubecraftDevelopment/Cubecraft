package net.cubecraft.util.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public abstract class BlockableEventLoop<C extends Runnable> implements Executor {
   private final String name;
   private static final Logger LOGGER = LogManager.getLogger("BlockableEventLoop");
   private final Queue<C> pending = new ConcurrentLinkedQueue<>();
   private int blockingCount;

   protected BlockableEventLoop(String name) {
      this.name = name;
   }

   protected abstract C of(Runnable command);

   protected abstract boolean shouldRun(C command);

   public boolean checkThread() {
      return Thread.currentThread() == this.getOwnerThread();
   }

   protected abstract Thread getOwnerThread();

   protected boolean scheduleExecutables() {
      return !this.checkThread();
   }

   public Queue<C> getPending() {
      return pending;
   }

   public String name() {
      return this.name;
   }

   public <V> CompletableFuture<V> submit(Supplier<V> command) {
      return this.scheduleExecutables() ? CompletableFuture.supplyAsync(command, this) : CompletableFuture.completedFuture(command.get());
   }

   private CompletableFuture<Void> submitAsync(Runnable command) {
      return CompletableFuture.supplyAsync(() -> {
         command.run();
         return null;
      }, this);
   }

   public CompletableFuture<Void> submit(Runnable command) {
      if (this.scheduleExecutables()) {
         return this.submitAsync(command);
      } else {
         command.run();
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   public void executeBlocking(Runnable command) {
      if (!this.checkThread()) {
         this.submitAsync(command).join();
      } else {
         command.run();
      }
   }

   public void tell(C command) {
      this.pending.add(command);
      LockSupport.unpark(this.getOwnerThread());
   }

   public void execute(Runnable command) {
      if (this.scheduleExecutables()) {
         this.tell(this.of(command));
      } else {
         command.run();
      }

   }

   public void executeIfPossible(Runnable p_201937_) {
      this.execute(p_201937_);
   }

   protected void drop() {
      this.pending.clear();
   }

   protected void runAll() {
      while(this.pollEvents()) {
      }

   }

   public boolean pollEvents() {
      C r = this.pending.peek();
      if (r == null) {
         return false;
      } else if (this.blockingCount == 0 && !this.shouldRun(r)) {
         return false;
      } else {
         this.runCommand(this.pending.remove());
         return true;
      }
   }

   public void managedBlock(BooleanSupplier supplier) {
      ++this.blockingCount;

      try {
         while(!supplier.getAsBoolean()) {
            if (!this.pollEvents()) {
               this.park();
            }
         }
      } finally {
         --this.blockingCount;
      }

   }

   protected void park() {
      Thread.yield();
      LockSupport.parkNanos("waiting for tasks", 100000L);
   }

   protected void runCommand(C command) {
      try {
         command.run();
      } catch (Exception exception) {
         LOGGER.error("Error executing task on {}", this.name(), exception);
      }

   }
}